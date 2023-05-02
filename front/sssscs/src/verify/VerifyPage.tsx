import { Box, Button, MenuItem, Select, TextField } from "@mui/material";
import { useEffect, useState } from "react";
import { FieldValues, useForm } from "react-hook-form";
import { Navigate, useLocation, useNavigate } from "react-router-dom";
import { VerificationCodeSendRequestDTO, VerificationCodeVerifyDTO, VerificationMethod, VerificationReason, VerifyPageRouterState, VerifyService } from "./VerifyService";
import { AxiosError, AxiosResponse } from "axios";

export const VerifyPage = () => {
    const { setValue, register, handleSubmit, formState: {errors}, setError} = useForm({mode: 'all'});
    const { register: register2, handleSubmit: handleSubmit2, formState: {errors: errors2}, setError: setError2} = useForm({mode: 'all'});
    const { state: routerLocationState } = useLocation();
    let [email, setEmail] = useState<string>("");
    let navigate = useNavigate();

    useEffect(() => {
        const state = routerLocationState as VerifyPageRouterState;
        let emailFromState: string | null = state?.email;
        setValue('email', emailFromState);
        setEmail(emailFromState);
    }, []);
    
    const sendCode = async (data: FieldValues) => {
        const dto: VerificationCodeSendRequestDTO = {
            userEmail: data['email'],
            method: data['type'] as VerificationMethod,
            dontActuallySend: true,
            reason: VerificationReason.TWO_FA,
        }
        setEmail(data['email']);

        console.log(dto);
        VerifyService.sendCode(dto)
            .then((val: AxiosResponse<void>) => {
                console.log("Success!");
            })
            .catch((err : AxiosError) => {
                if (err.response?.status == 404) {
                    setError('email', {message: "User not found."}, {shouldFocus: true});
                } else if (err.response?.status === 429) {
                    setError('password', {message: 'Too many attempts. This account is temporarily blocked.'}, {shouldFocus: true});
                }
            }); 
    }

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
                setError2('code', {message: err.response?.data as string}, {shouldFocus: true});
            });
    }

    return (
        <>
            <Box component='form' noValidate onSubmit={handleSubmit(sendCode)}>
                <TextField
                    disabled={true}
                    type="email"
                    label="Your account email"
                    {...register('email', { required: 'Please enter your email'})}
                    error={!!errors['email']}
                    helperText={errors['email']?.message?.toString()}
                    />

                <Select
                    labelId="verification-type"
                    required
                    defaultValue={VerificationMethod.EMAIL}
                    {...register('type', { required: 'Type is required' })}
                    error={!!errors['type']}
                    >
                        <MenuItem value={VerificationMethod.EMAIL}>Email</MenuItem>
                        <MenuItem value={VerificationMethod.SMS}>SMS</MenuItem>
                    </Select>

                <Button variant="contained" type="submit">
                    Send Verification Code
                </Button>
            </Box>

            <Box component='form' noValidate onSubmit={handleSubmit2(verifyCode)}>
                <TextField
                    label="Code"
                    {...register2('code', { 
                        required: 'Please enter the code', 
                        pattern: {
                            value: /^[0-9]{6}$/,
                            message: "Code must be 6-digits"
                        }
                    })}
                    error={!!errors2['code']}
                    helperText={errors2['code']?.message?.toString()}
                    />

                <Button variant="contained" type="submit">
                    Verify
                </Button>
            </Box>
        </>
    )
}