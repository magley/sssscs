import { Box, Button, MenuItem, Select, TextField } from "@mui/material";
import { useEffect, useState } from "react";
import { FieldValues, useForm } from "react-hook-form";
import { useLocation } from "react-router-dom";
import { VerificationCodeSendRequestDTO, VerificationCodeVerifyDTO, VerificationMethod, VerifyPageRouterState } from "./VerifyService";



export const VerifyPage = () => {
    const { setValue, register, control, handleSubmit, formState: {errors}} = useForm({mode: 'all'});
    const { register: register2 } = useForm({mode: 'all'});
    const { state: routerLocationState } = useLocation();
    let [email, setEmail] = useState<string>("");

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
        }

        console.log(dto);
        setEmail(data['email']);
    }

    const verifyCode = async (data: FieldValues) => {
        const dto: VerificationCodeVerifyDTO = {
            userEmail: email,
            code: data['code'],
        };

        console.log(dto);
    }

    return (
        <>
            <Box component='form' noValidate onSubmit={handleSubmit(sendCode)}>
                <TextField
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