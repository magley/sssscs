import React, { FormEvent, useState } from "react";
import Button from '@mui/material/Button';
import { TextField, Box } from '@mui/material';
import { LoginService, UserLoginDto, UserLoginResultDto } from "./LoginService";
import { AxiosError, AxiosResponse } from "axios";

/**
 * Send a login request to the server.
 * @param data Login credentials.
 */
const TryLogin = async (data: UserLoginDto) => {
    LoginService.login(data)
        .then((res: AxiosResponse<UserLoginResultDto>) => {
            console.log(res.data);
            console.log(res.status);
        })
        .catch((err : AxiosError) => {
            console.error(err.response?.data);
            console.error(err.response?.status);
        });
}

export const Login : React.FC<{}> = () => {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    /**
     * Callback for when the user clicks on the 'Sign In' button.
     */
    const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        TryLogin({
            email, password
        });
    };

    return (
        <div className="Login">
            <h2>Login</h2>
            <form onSubmit={handleSubmit}>
                <Box>
                    <TextField variant="outlined" type="text" name="cert-email" label="Email" required onChange={e => setEmail(e.target.value)} />
                    <br/>
                    <TextField variant="outlined" type="password" name="cert-pass" label="Password" required onChange={e => setPassword(e.target.value)} />
                    <br/>
                    <Button color="primary" variant="contained" type="submit">Sign In</Button>
                </Box>
            </form>
        </div>
    );
}