import { Box, Button, TextField, Typography } from "@mui/material";
import { FieldValues, useForm } from "react-hook-form"
import { CertService } from "./CertService";
import { AxiosError, AxiosResponse } from "axios";
import { useState } from "react";

export const CertVerify = () => {
    const { register, handleSubmit, setError, formState: {errors}} = useForm({mode: 'all'});
    let [ isValid, setIsValid ] = useState<Boolean | null>(null);

    const verifyBasedOnForm = async (data: FieldValues) => {
        const certID = data['id'] as number;

        CertService.verify(certID)
            .then((res: AxiosResponse<Boolean>) => {
                setIsValid(res.data);
            }).catch((err: AxiosError) => {
                if (err.response?.status === 404) {
                    setError('id', {message: 'Certificate not found'}, {shouldFocus: true});
                }
                setIsValid(null);
            });
    }

    return (
        <>
            <Box component='form' noValidate onSubmit={handleSubmit(verifyBasedOnForm)}>
                <Typography variant='h4'>
                    Verify certificate based on ID
                </Typography>

                <TextField
                    sx={{mb: 2}}
                    label="Id"
                    fullWidth
                    required
                    type="number"
                    {...register('id', { required: 'ID is required' })}
                    error={!!errors['id']}
                    helperText={errors['id']?.message?.toString()}
                />

                <Button variant='contained' type='submit'>Verify</Button>
            </Box>
            {
                isValid != null &&
                <p>
                    {isValid ? "Certificate is valid" : "Certificate is not valid"}
                </p>
            }
        </>
    )
}