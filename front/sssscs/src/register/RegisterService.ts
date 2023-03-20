import axios, { AxiosResponse } from "axios";
import { Env } from "../common/Environment";

export interface UserCreateDto {
    email: string,
    password: string,
    name: string,
    surname: string,
};

// TODO: Remove, temporary
export interface User {
    id: number,
    email: string,
    password: string,
    name: string,
    surname: string,
}

export class RegisterService {
    static async register(dto: UserCreateDto): Promise<AxiosResponse<User>> {
        return await axios.post(`${Env.url}/api/user`, dto);
    }
};