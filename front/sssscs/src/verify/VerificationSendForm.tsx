import { TextField, Select, MenuItem, Button } from "@mui/material"
import { Box } from "@mui/system"
import { AxiosResponse, AxiosError } from "axios"
import React, { useEffect } from "react"
import { FieldValues, useForm } from "react-hook-form"
import { VerificationCodeSendRequestDTO, VerificationMethod, VerificationReason, VerifyService } from "./VerifyService"

export const VerificationSendForm = ({email, setEmail, reason, emailReadOnly} :
    {email: string, setEmail: React.Dispatch<string>, reason: VerificationReason, emailReadOnly: boolean}) => {
    const { setValue, register, handleSubmit, formState: {errors}, setError} = useForm({mode: 'all'});

    useEffect(() => {
        setValue('email', email);
    }, [email, setValue])

    const sendCode = async (data: FieldValues) => {
        const dto: VerificationCodeSendRequestDTO = {
            userEmail: data['email'],
            method: data['type'] as VerificationMethod,
            dontActuallySend: true,
            reason: reason,
        }
        setEmail(data['email']);

        console.log(dto);
        VerifyService.sendCode(dto)
            .then((val: AxiosResponse<void>) => {
                console.log("Success!");
            })
            .catch((err : AxiosError) => {
                if (err.response?.status === 404) {
                    setError('email', {message: "User not found."}, {shouldFocus: true});
                } else if (err.response?.status === 429) {
                    setError('password', {message: 'Too many attempts. This account is temporarily blocked.'}, {shouldFocus: true});
                }
            }); 
    }

    return (
        <Box component='form' noValidate onSubmit={handleSubmit(sendCode)}>
        <TextField
            type="email"
            label="Your account email"
            disabled={emailReadOnly}
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
    )
}