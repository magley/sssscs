import { Box, TextField, Button, Typography } from "@mui/material";
import { AxiosResponse, AxiosError } from "axios";
import { RegisterService, UserCreateDto } from "./RegisterService";
import { FieldValues, useForm } from "react-hook-form";

const tryRegister = async (data: FieldValues) => {
    const dto: UserCreateDto = {
        email: data['email'],
        name: data['name'],
        surname: data['surname'],
        password: data['password'],
        phoneNumber: data['phoneNumber']
    }
    RegisterService.register(dto)
        .then((res: AxiosResponse<null>) => {
            console.log(res.data);
            console.log(res.status);
        })
        .catch((err : AxiosError) => {
            console.error(err.response?.data);
            console.error(err.response?.status);
        });
}

const Register = () => {
    const { register, trigger, handleSubmit, formState: { errors } } = useForm({mode: 'all'});

    // TODO: Find a cleaner way to do this
    const { onChange, onBlur, name, ref } = register('password', {
        required: 'Password is required',
        pattern: {
            value: /^(?=.*\d)(?=.*[A-Z])(?!.*[^a-zA-Z0-9@#$^+=])(.{8,15})$/,
            message: 'Password must contain only characters a-z, A-Z, 0-9, @, #, $, ^, +, = and must be between 8 and 15 characters and contain at least one digit and capital letter!'
        }
    });

    return (
        <Box component='form' noValidate onSubmit={handleSubmit(tryRegister)} sx={{maxWidth: '30rem'}}>
            <Typography variant='h4' sx={{mb: 2}}>
                Register
            </Typography>
            <TextField
                sx={{mb: 2}}
                label="Email"
                fullWidth
                required
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
                required
                // TODO: Find a cleaner way to do this
                onChange={(e) => {
                    onChange(e);
                    trigger('confirmPassword');
                }}
                onBlur={onBlur}
                name={name}
                ref={ref}
                error={!!errors['password']}
                helperText={errors['password']?.message?.toString()}
            />
            <TextField
                sx={{mb: 2}}
                label="Confirm password"
                type="password"
                fullWidth
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
                required
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
                required
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
                required
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