import axios, { AxiosResponse } from "axios";

export interface UserLoginDto {
    email: string,
    password: string,
};

export interface UserLoginResultDto {
    todo: string,
};

export class LoginService {
    static async login(dto: UserLoginDto): Promise<AxiosResponse<UserLoginResultDto>> {
        const env_url = "http://127.0.0.1:8080";
        return await axios.put(`${env_url}/api/user/login`, dto);
    }
};