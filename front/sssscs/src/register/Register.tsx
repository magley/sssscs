import { Box, TextField, Button } from "@mui/material";
import { AxiosResponse, AxiosError } from "axios";
import { FormEvent, useState } from "react";
import { RegisterService, User, UserCreateDto } from "./RegisterService";

const TryRegister = async (dto: UserCreateDto) => {
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

let Register = () => {
    const [email, setEmail] = useState("");
    const [name, setName] = useState("");
    const [surname, setSurname] = useState("")
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");

    const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        TryRegister({email, password, name, surname})
    }

    return (
        <div className="Register">
            <h2>Register</h2>
            <form onSubmit={handleSubmit}>
                <Box>
                    <TextField variant="outlined" type="text" name="cert-email" label="Email" required onChange={e => setEmail(e.target.value)} />
                    <br/>
                    <TextField variant="outlined" type="password" name="cert-pass" label="Password" required onChange={e => setPassword(e.target.value)} />
                    <br/>
                    <TextField variant="outlined" type="password" name="cert-confirm-pass" label="Confirm password" required onChange={e => setConfirmPassword(e.target.value)} />
                    <br/>
                    <TextField variant="outlined" type="text" name="cert-name" label="Name" required onChange={e => setName(e.target.value)} />
                    <br/>
                    <TextField variant="outlined" type="text" name="cert-surname" label="Surname" required onChange={e => setSurname(e.target.value)} />
                    <br/>
                    <Button color="primary" variant="contained" type="submit">Register</Button>
                </Box>
            </form>
        </div>
    );
}

export default Register;