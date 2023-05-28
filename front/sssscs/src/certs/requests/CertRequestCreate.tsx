import { Box, Button, InputLabel, MenuItem, Select, TextField, Typography } from "@mui/material";
import { FieldValues, useForm } from "react-hook-form";
import { CertType } from "../CertService";
import { useEffect, useState } from "react";
import { AuthService } from "../../auth/AuthService";
import { CertRequestCreateDTO, CertRequestService } from "./CertRequestService";
import ReCAPTCHA from "react-google-recaptcha";


export const CertRequestCreate = () => {
    const { register, handleSubmit, formState: { errors }, watch, setError} = useForm({mode: 'all'});
    const [ allowedTypes, setAllowedTypes ] = useState<Array<CertType>>([]);
    const [token, setToken] = useState<string | null>("");
    const [ captchaError, setCaptchaError ] = useState("");

    useEffect(() => {
        let t = [CertType.END, CertType.INTERMEDIATE, CertType.ROOT];
        if (AuthService.getRole() !== 'ADMIN') {
            t.pop();
        }

        setAllowedTypes(t);
    }, []);

    const sendRequest = async (data: FieldValues) => {
        setCaptchaError("");
        if (!token) {
            setCaptchaError("Retry captcha");
            return;
        }
        const dto: CertRequestCreateDTO = {
            type: CertType[data['type']],
            parentId: Number(data['parentId']),
            subjectData: {
                name: data['subjectName'],
                surname: data['subjectSurname'],
                email: data['subjectEmail'],
                organization: data['subjectOrganization'],
                commonName: data['subjectCommonName'],
            },
            token: token,
        };

        if (dto.type !== CertType[CertType.ROOT]) {
            if (dto.parentId == null || dto.parentId <= 0) {
                console.error("Need parent!");
                setError('parentId', {message: "Need parent!"}, {shouldFocus: true});
                return;
            }
        } else {
            dto.parentId = null;
        }
          
        CertRequestService.create(dto)
            .then((v) => {
                console.log(v.data);
                console.log(v.status);
            })
            .catch((err) => {
                if (err.response?.status === 422) {
                    setCaptchaError(err.response?.data as string);
                }
                else {
                    console.error(err.response.data);
                    setError('parentId', {message: err.response.data}, {shouldFocus: true});
                }
            })
    }

    return (
        <>
            <Box component='form' noValidate onSubmit={handleSubmit(sendRequest)}>
                <Typography variant='h4'>Create new certificate</Typography>

                <InputLabel id="cert-type">Type</InputLabel>
                <Select
                    labelId="cert-type"
                    defaultValue={CertType.END}
                    required
                    {...register('type', { required: 'Type is required' })}
                    error={!!errors['type']}
                    >
                        { allowedTypes.map((t: CertType) => (
                            <MenuItem key={t} value={t}>{CertType[t]}</MenuItem>
                        ))}
                </Select>
                <br/>
                    
                <TextField
                    label="Parent certificate ID"
                    disabled={watch("type") === CertType.ROOT}
                    required={watch("type") !== CertType.ROOT}
                    value={watch("type") === CertType.ROOT ? 0 : (watch('parentId') || 0)}
                    {...register('parentId', { required: 'Parent ID is required' })}
                    error={!!errors['parentId']}
                    helperText={errors['parentId']?.message?.toString()}
                    />
                <br/>

                <InputLabel>Subject</InputLabel>
                <Box>
                    <TextField 
                        label="Common Name"
                        required
                        {...register('subjectCommonName', {required: "Subject's common name is required"})}
                        error={!!errors['subjectCommonName']}
                        helperText={errors['subjectCommonName']?.message?.toString()}
                        />
                    <TextField 
                        label="Name"
                        {...register('subjectName')}
                        />
                     <TextField 
                        label="Surname"
                        {...register('subjectSurname')}
                        />
                    <TextField 
                        label="Email"
                        type="email"
                        {...register('subjectEmail')}
                        />
                        <TextField 
                        label="Organization"
                        {...register('subjectOrganization')}
                        />
                </Box>

                <Button variant="contained" type="submit">
                    Send Request
                </Button>
            </Box>
            <ReCAPTCHA
                sitekey={process.env.REACT_APP_RECAPTCHA_SITE_KEY!}
                onChange={(value) => setToken(value)}/>
            { captchaError !== "" && (
            <>
            {captchaError}
            </>
            )}
        </>
    );
}