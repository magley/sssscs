import { Box, Button, Typography } from "@mui/material";
import { useForm } from "react-hook-form"
import { CertService } from "./CertService";
import { AxiosError, AxiosResponse } from "axios";
import { useRef, useState } from "react";
import UploadIcon from "@mui/icons-material/Upload"
import ReCAPTCHA from "react-google-recaptcha";

export const CertVerifyFile = () => {
    const { register, handleSubmit, setError, formState: {errors}} = useForm({mode: "all"});
    const fileInput = useRef<HTMLInputElement>(null);
    let [ fileName, setFileName ] = useState<string | null>(null);
    let [ isValid, setIsValid ] = useState<Boolean | null>(null);
    const [token, setToken] = useState<string | null>("");
    const [ captchaError, setCaptchaError ] = useState("");

    register("certFile");

    const verifyBasedOnForm = async () => {
        const fileList = fileInput.current?.files;
        let file = null;
        if (fileList) {
            file = fileList[0];
        }
        if (!file) {
            setError("certFile", {message: "You need to upload a cert file first."}, {shouldFocus: true});
            setIsValid(null);
            return;
        }
        setCaptchaError("");
        if (!token) {
            setCaptchaError("Retry captcha");
            return;
        }
        CertService.verifyFile(file, token)
            .then((res: AxiosResponse<Boolean>) => {
                setIsValid(res.data);
            }).catch((err: AxiosError) => {
                if (err.response?.status === 422) {
                    setCaptchaError(err.response?.data as string);
                }
                else {
                    setError("certFile", {message: err.response?.data as string}, {shouldFocus: true});
                    setIsValid(null);
                }
            });
    }

    const handleUploadClick = () => {
        fileInput.current && fileInput.current.click()
    }

    const handleFileChange = () => {
        if (fileInput.current?.files && fileInput.current.files[0].name) {
            setFileName(fileInput.current.files[0].name)
        }
    }

    return (
        <>
            <Box component="form" noValidate onSubmit={handleSubmit(verifyBasedOnForm)}>
                <Typography variant="h4">
                    Verify certificate based on uploaded .crt file
                </Typography>

                <input
                    type="file"
                    accept=".crt"
                    name="certFile"
                    style={{display: "none"}}
                    ref={fileInput}
                    onChange={() => handleFileChange()}
                />

                <Button variant="contained" type="button" startIcon={<UploadIcon/>}
                    onClick={() => handleUploadClick()}>
                    {fileName || "Upload a .crt file"}
                </Button>
                <br/>

                <Button variant="contained" type="submit">Verify</Button>
            </Box>
            <ReCAPTCHA
                sitekey={process.env.REACT_APP_RECAPTCHA_SITE_KEY!}
                onChange={(value) => setToken(value)}/>
            {
                isValid !== null &&
                <p>
                    {isValid ? "Certificate is valid" : "Certificate is not valid"}
                </p>
            }
            {
                !!errors["certFile"] && <p style={{color: "red"}}>{errors["certFile"]?.message?.toString()}</p>
            }
            { captchaError !== "" && (
            <>
            {captchaError}
            </>
        )}
        </>
    )
}