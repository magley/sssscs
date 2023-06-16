import { AxiosResponse } from "axios";
import { axiosInstance } from "../http/HttpService";

export interface UserLoginDto {
    email: string,
    password: string,
    token: string,
};

export class LoginService {
    static async login(dto: UserLoginDto): Promise<AxiosResponse<string>> {
        return await axiosInstance.post('user/session/login', dto);
    }
};