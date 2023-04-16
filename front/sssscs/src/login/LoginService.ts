import axios, { AxiosResponse } from "axios";
import { Env } from "../common/Environment";
import {Buffer} from 'buffer';

export interface UserLoginDto {
    email: string,
    password: string,
};

export class LoginService {
    static async login(dto: UserLoginDto): Promise<AxiosResponse<string>> {
        return await axios.post(`${Env.url}/api/user/session/login`, dto);
    }
};