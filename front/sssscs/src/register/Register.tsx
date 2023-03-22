import { Box, TextField, Button, Typography } from "@mui/material";
import { AxiosResponse, AxiosError } from "axios";
import { RegisterService, User, UserCreateDto } from "./RegisterService";
import { FieldValues, useForm } from "react-hook-form";

const TryRegister = async (data: FieldValues) => {
    const dto: UserCreateDto = {
        'email': data['email'],
        'name': data['name'],
        'surname': data['surname'],
        'password': data['password']
    }
    RegisterService.register(dto)
        .then((res: AxiosResponse<User>) => {
            console.log(res.data);
            console.log(res.status);
        })
        .catch((err : AxiosError) => {
            console.error(err.response?.data);
            console.error(err.response?.status);
        });
}

const Register = () => {
    const { register, handleSubmit, formState: { errors } } = useForm({mode: 'onChange'});

    return (
        <Box component='form' onSubmit={handleSubmit(TryRegister)} sx={{maxWidth: '30rem'}}>
            <Typography variant='h4' sx={{mb: 2}}>
                Register
            </Typography>
            <TextField
                sx={{mb: 2}}
                label="Email"
                fullWidth
                {...register('email', {
                    required: 'Email is required',
                    pattern: {
                        value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}$/i,
                        message: 'Not a valid email'
                    }
                })}
                error={!!errors['email']}
                helperText={errors['email']?.message?.toString()}
            />
            <TextField
                sx={{mb: 2}}
                label="Password"
                type="password"
                fullWidth
                {...register('password', {
                    required: 'Password is required'
                })}
                error={!!errors['password']}
                helperText={errors['password']?.message?.toString()}
            />
            <TextField
                sx={{mb: 2}}
                label="Confirm password"
                type="password"
                fullWidth
                // FIXME: Update when password is updated as well
                {...register('confirmPassword', {
                    validate: (value, formValues) => {
                        return value === formValues['password'] || 'Password not matching'
                    }
                })}
                error={!!errors['confirmPassword']}
                helperText={errors['confirmPassword']?.message?.toString()}
            />
            <TextField
                sx={{mb: 2}}
                label="Name"
                fullWidth
                {...register('name')}
            />
            <TextField
                sx={{mb: 2}}
                label="Surname"
                fullWidth
                {...register('surname')}
            />
            <Button variant="contained" type="submit">
                Register
            </Button>
        </Box>
    );
}

export default Register;