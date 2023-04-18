import { AxiosResponse } from "axios";
import { axiosInstance } from "../http/HttpService";

export interface UserCreateDto {
    email: string,
    password: string,
    name: string,
    surname: string,
    phoneNumber: string
};

export class RegisterService {
    static async register(dto: UserCreateDto): Promise<AxiosResponse<null>> {
        return await axiosInstance.post('user/session/register', dto);
    }
};