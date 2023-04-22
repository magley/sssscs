import { Box, Button, MenuItem, Select, TextField } from "@mui/material";
import { FieldValues, useForm } from "react-hook-form";

export enum VerificationMethod {
    EMAIL, SMS
};

export interface VerificationCodeSendRequestDTO {
    userEmail: string,
    method: VerificationMethod,
    dontActuallySend: boolean
};

export const VerifyPage = () => {
    const { register, control, handleSubmit, formState: {errors}} = useForm({mode: 'all'});
    
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