import { Box, TextField, Button, Typography } from "@mui/material";
import { AxiosResponse, AxiosError } from "axios";
import { RegisterService, User, UserCreateDto } from "./RegisterService";
import { FieldValues, useForm } from "react-hook-form";

const TryRegister = async (data: FieldValues) => {
    const dto: UserCreateDto = {
        email: data['email'],
        name: data['name'],
        surname: data['surname'],
        password: data['password'],
        phoneNumber: data['phoneNumber']
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
                    },
                    maxLength: {
                        value: 100,
                        message: 'Email must be less than 101 characters long'
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
                    required: 'Password is required',
                    maxLength: {
                        value: 18,
                        message: 'Password must be less than 19 characters long'
                    }
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
                {...register('name', {
                    required: 'Name is required',
                    maxLength: {
                        value: 100,
                        message: 'Name must be less than 101 characters long'
                    }
                })}
                error={!!errors['name']}
                helperText={errors['name']?.message?.toString()}
            />
            <TextField
                sx={{mb: 2}}
                label="Surname"
                fullWidth
                {...register('surname', {
                    required: 'Surname is required',
                    maxLength: {
                        value: 100,
                        message: 'Surname must be less than 101 characters long'
                    }
                })}
                error={!!errors['surname']}
                helperText={errors['surname']?.message?.toString()}
            />
            <TextField
                sx={{mb: 2}}
                label="Phone number"
                fullWidth
                {...register('phoneNumber', {
                    required: 'Phone number is required',
                    maxLength: {
                        value: 18,
                        message: 'Phone number must be less than 19 characters long'
                    }
                })}
                error={!!errors['phoneNumber']}
                helperText={errors['phoneNumber']?.message?.toString()}
            />
            <Button variant="contained" type="submit">
                Register
            </Button>
        </Box>
    );
}

export default Register;