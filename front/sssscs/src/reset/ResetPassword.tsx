import { TextField, Button } from "@mui/material";
import { Box } from "@mui/system";
import { AxiosError, AxiosResponse } from "axios";
import { useState } from "react";
import { FieldValues, useForm } from "react-hook-form";
import { useNavigate } from "react-router";
import { VerificationSendForm } from "../verify/VerificationSendForm"
import { VerificationCodeResetDTO, VerificationReason, VerifyService } from "../verify/VerifyService";

export const ResetPassword = () => {
    const { register, handleSubmit, formState: {errors}, setError, trigger} = useForm({mode: 'all'});
    let [email, setEmail] = useState<string>("");
    let navigate = useNavigate();

    const { onChange, onBlur, name, ref } = register('newPassword', {
        required: 'Password is required',
        pattern: {
            value: /^(?=.*\d)(?=.*[A-Z])(?!.*[^a-zA-Z0-9@#$^+=])(.{8,15})$/,
            message: 'Password must contain only characters a-z, A-Z, 0-9, @, #, $, ^, +, = and must be between 8 and 15 characters and contain at least one digit and capital letter!'
        }
    });

    const resetPassword = async (data: FieldValues) => {
        const dto: VerificationCodeResetDTO = {
            userEmail: email,
            code: data['code'],
            newPassword: data['newPassword'],
        }
        console.log(dto);
        VerifyService.resetPassword(dto)
            .then((val: AxiosResponse<void>) => {
                console.log("Successfully reset password");
                navigate("/login");
            })
            .catch((err: AxiosError) => {
                setError('code', {message: err.response?.data as string}, {shouldFocus: true});
            })
    }

    return (
        <>
        <VerificationSendForm email={email} setEmail={setEmail} reason={VerificationReason.RESET_PASSWORD} emailReadOnly={false} />
        <Box component='form' noValidate onSubmit={handleSubmit(resetPassword)} sx={{maxWidth: '30rem', mt: 2}}>
            <TextField
                sx={{mb: 2}}
                fullWidth
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
            <TextField
                sx={{mb: 2}}
                fullWidth
                label="New password"
                type="password"
                onChange={(e) => {
                    onChange(e);
                    trigger('confirmNewPassword');
                }}
                onBlur={onBlur}
                name={name}
                ref={ref}
                error={!!errors['newPassword']}
                helperText={errors['newPassword']?.message?.toString()}
            />
            <TextField
                sx={{mb: 2}}
                fullWidth
                label="Confirm new password"
                type="password"
                {...register('confirmNewPassword', {
                    validate: (value, formValues) => {
                        return value === formValues['newPassword'] || 'Password not matching'
                    }
                })}
                error={!!errors['confirmNewPassword']}
                helperText={errors['confirmNewPassword']?.message?.toString()}
            />

            <Button variant="contained" type="submit">
                Verify
            </Button>
        </Box>
        </>
    )
}