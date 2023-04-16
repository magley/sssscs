import axios, { AxiosResponse } from "axios";
import { Env } from "../common/Environment";

export interface UserLoginDto {
    email: string,
    password: string,
};

export interface UserLoginResultDto {
    todo: string,
};

export class LoginService {
    static async login(dto: UserLoginDto): Promise<AxiosResponse<UserLoginResultDto>> {
        return await axios.put(`${Env.url}/api/user/login`, dto);
    }
};