import Button from '@mui/material/Button';
import { TextField, Box, Typography } from '@mui/material';
import { LoginService, UserLoginDto } from "./LoginService";
import { AxiosError, AxiosResponse } from "axios";
import { FieldValues, useForm } from "react-hook-form";
import { AuthService } from '../auth/AuthService';
import { Link, useNavigate } from 'react-router-dom';
import { GlobalState } from '../App';
import { VerifyPageRouterState } from '../verify/VerifyService';
import { useState } from 'react';
import ReCAPTCHA from 'react-google-recaptcha';


export const Login = (props: {gloState: GlobalState}) => {
    const { register, handleSubmit, formState: { errors }, setError } = useForm({mode: 'all'});
    const navigate = useNavigate();
    const [token, setToken] = useState<string | null>("");
    const [ captchaError, setCaptchaError ] = useState("");

    const tryLogin = async (data: FieldValues) => {
        setCaptchaError("");
        if (!token) {
            setCaptchaError("Retry captcha");
            return;
        }
        const dto: UserLoginDto = {
            email: data['email'],
            password: data['password'],
            token: token,
        }
        LoginService.login(dto)
            .then((res: AxiosResponse<string>) => {
                AuthService.putJWT(res.data);
                props.gloState.updateIsLoggedIn();
                navigate("/certificates");
            }).catch((err : AxiosError) => {
                console.error(err.response?.data);
                console.error(err.response?.status);

                if (err.response?.status === 400) {
                    setError('password', {message: 'Wrong email or password'}, {shouldFocus: true});
                } else if (err.response?.status === 422) {
                    const routedState: VerifyPageRouterState = {
                        email: dto.email
                    };
                    navigate("/verify", {state: routedState});
                } else if (err.response?.status === 429) {
                    setError('password', {message: 'We have detected unusual activity from your account and have put a temporary block for safety purposes.'}, {shouldFocus: true});
                } else if (err.response?.status === 406) {
                    const routedState: VerifyPageRouterState = {
                        email: dto.email
                    };
                    navigate("/update-password", {state: routedState});
                } else if (err.response?.status === 418) {
                    setCaptchaError(err.response?.data as string);
                }
            });
    }

    return (
        <>
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
        <Link to='/reset-password'>Forgot password?</Link>
        <ReCAPTCHA
            sitekey={process.env.REACT_APP_RECAPTCHA_SITE_KEY!}
            onChange={(value) => setToken(value)}/>
        { captchaError !== "" && (
        <>
        {captchaError}
        </>
        )}
        </>
    );
}