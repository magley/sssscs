import Button from '@mui/material/Button';
import { TextField, Box, Typography } from '@mui/material';
import { LoginService, UserLoginDto, UserLoginResultDto } from "./LoginService";
import { AxiosError, AxiosResponse } from "axios";
import { FieldValues, useForm } from "react-hook-form";

/**
 * Send a login request to the server.
 * @param data Login credentials.
 */
const tryLogin = async (data: FieldValues) => {
    const dto: UserLoginDto = {
        email: data['email'],
        password: data['password']
    }
    LoginService.login(dto)
        .then((res: AxiosResponse<UserLoginResultDto>) => {
            console.log(res.data);
            console.log(res.status);
        })
        .catch((err : AxiosError) => {
            console.error(err.response?.data);
            console.error(err.response?.status);
        });
}

export const Login = () => {
    const { register, handleSubmit, formState: { errors } } = useForm({mode: 'all'});

    return (
        <Box component='form' noValidate onSubmit={handleSubmit(tryLogin)} sx={{maxWidth: '30rem'}}>
            <Typography variant='h4' sx={{mb: 2}}>
                Login
            </Typography>
            <TextField
                sx={{mb: 2}}
                label="Email"
                fullWidth
                required
                {...register('email', { required: 'Email is required' })}
                error={!!errors['email']}
                helperText={errors['email']?.message?.toString()}
            />
            <TextField
                sx={{mb: 2}}
                type="password"
                label="Password"
                fullWidth
                required
                {...register('password', { required: 'Password is required' })}
                error={!!errors['password']}
                helperText={errors['password']?.message?.toString()}
            />
            <Button variant="contained" type="submit">
                Sign In
            </Button>
        </Box>
    );
}