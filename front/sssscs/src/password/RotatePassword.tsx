import { useEffect, useState } from "react";
import { FieldValues, useForm } from "react-hook-form";
import { useLocation, useNavigate } from "react-router-dom";
import { PasswordRotationDTO, PasswordService } from "./PasswordService";
import { Box, Button, TextField } from "@mui/material";
import { AxiosResponse, AxiosError } from "axios";
import { VerifyPageRouterState } from "../verify/VerifyService";

export const RotatePassword = () => {
    const { register, trigger, handleSubmit, formState: {errors}, setError} = useForm({mode: 'all'});
    let [email, setEmail] = useState<string>("");
    let navigate = useNavigate();
    const { state: routerLocationState } = useLocation();

    useEffect(() => {
        const state = routerLocationState as VerifyPageRouterState;
        let emailFromState: string | null = state?.email;
        setEmail(emailFromState);
    }, [routerLocationState]);

    const { onChange, onBlur, name, ref } = register('newPassword', {
        required: 'Password is required',
        pattern: {
            value: /^(?=.*\d)(?=.*[A-Z])(?!.*[^a-zA-Z0-9@#$^+=])(.{8,15})$/,
            message: 'Password must contain only characters a-z, A-Z, 0-9, @, #, $, ^, +, = and must be between 8 and 15 characters and contain at least one digit and capital letter!'
        }
    });

    const rotatePassword = async (data: FieldValues) => {
        const dto: PasswordRotationDTO = {
            userEmail: email,
            oldPassword: data['oldPassword'],
            newPassword: data['newPassword'],
        };

        PasswordService.rotate_password(dto)
        .then((res: AxiosResponse<null>) => {
            navigate("/login");
        })
        .catch((err : AxiosError) => {
            if (err.response?.status === 400) {
                setError('oldPassword', {message: err.response?.data as string}, {shouldFocus: true});
            }
        });
    }

    return (
        <>
            <p>
                In order to protect your account, we need you to change your password.
            </p>
            <Box component='form' noValidate onSubmit={handleSubmit(rotatePassword)}>
                <TextField
                    type="password"
                    label="Enter your old password"
                    {...register('oldPassword', { required: 'This field is required.'})}
                    error={!!errors['oldPassword']}
                    helperText={errors['oldPassword']?.message?.toString()}
                    />

                <br/>

                <TextField
                    type="password"
                    label="Enter your new password"
                    {...register('newPassword', { required: 'This field is required.'})}
                    error={!!errors['newPassword']}
                    helperText={errors['newPassword']?.message?.toString()}
                    onChange={(e) => {
                        onChange(e);
                        trigger('newPasswordConfirm');
                    }}
                    />

                <br/>

                <TextField
                    type="password"
                    label="Confirm new password"
                    {...register('newPasswordConfirm', { validate: (value, formValues) => {
                        return value === formValues['newPassword'] || 'Passwords not matching'
                    }})}
                    error={!!errors['newPasswordConfirm']}
                    helperText={errors['newPasswordConfirm']?.message?.toString()}
                    />

                <br/>

                <Button variant="contained" type="submit">
                    Update email
                </Button>
            </Box>
        </>
    );
}