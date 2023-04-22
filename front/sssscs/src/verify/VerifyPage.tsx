import { Box, Button, MenuItem, Select, TextField } from "@mui/material";
import { useEffect } from "react";
import { FieldValues, useForm } from "react-hook-form";
import { useLocation } from "react-router-dom";
import { VerificationCodeSendRequestDTO, VerificationMethod, VerifyPageRouterState } from "./VerifyService";



export const VerifyPage = () => {
    const { setValue, register, control, handleSubmit, formState: {errors}} = useForm({mode: 'all'});
    const { state } = useLocation();

    useEffect(() => {
        const stateV = state as VerifyPageRouterState;
        let emailFromState: string | null = stateV?.email;
        setValue('email', emailFromState);
    }, []);
    
    const sendCode = async (data: FieldValues) => {
        const dto: VerificationCodeSendRequestDTO = {
            userEmail: data['email'],
            method: data['type'] as VerificationMethod,
            dontActuallySend: true,
        }

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
        </>
    )
}