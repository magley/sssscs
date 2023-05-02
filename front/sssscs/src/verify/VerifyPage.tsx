import { Box, Button, TextField } from "@mui/material";
import { useEffect, useState } from "react";
import { FieldValues, useForm } from "react-hook-form";
import { useLocation, useNavigate } from "react-router-dom";
import { VerificationCodeVerifyDTO, VerifyPageRouterState, VerifyService } from "./VerifyService";
import { AxiosError, AxiosResponse } from "axios";
import { VerificationSendForm } from "./VerificationSendForm";

export const VerifyPage = () => {
    const { register, handleSubmit, formState: {errors}, setError} = useForm({mode: 'all'});
    let [email, setEmail] = useState<string>("");
    let navigate = useNavigate();
    const { state: routerLocationState } = useLocation();

    useEffect(() => {
        const state = routerLocationState as VerifyPageRouterState;
        let emailFromState: string | null = state?.email;
        setEmail(emailFromState);
    }, []);

    const verifyCode = async (data: FieldValues) => {
        const dto: VerificationCodeVerifyDTO = {
            userEmail: email,
            code: data['code'],
        };

        console.log(dto);
        VerifyService.verifyUser(dto)
            .then((val: AxiosResponse<void>) => {
                navigate('/login');
            })
            .catch((err: AxiosError) => {
                setError('code', {message: err.response?.data as string}, {shouldFocus: true});
            });
    }

    return (
        <>
            <VerificationSendForm email={email} setEmail={setEmail}/>
            <Box component='form' noValidate onSubmit={handleSubmit(verifyCode)}>
                <TextField
                    label="Code"
                    {...register('code', { 
                        required: 'Please enter the code', 
                        pattern: {
                            value: /^[0-9]{6}$/,
                            message: "Code must be 6-digits"
                        }
                    })}
                    error={!!errors['code']}
                    helperText={errors['code']?.message?.toString()}
                    />

                <Button variant="contained" type="submit">
                    Verify
                </Button>
            </Box>
        </>
    )
}