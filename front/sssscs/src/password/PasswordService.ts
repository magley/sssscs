import { AxiosResponse } from "axios";
import { axiosInstance } from "../http/HttpService";

export interface PasswordRotationDTO {
    userEmail: string,
    oldPassword: string,
    newPassword: string,
};

export class PasswordService {
    static async rotate_password(dto: PasswordRotationDTO): Promise<AxiosResponse<null>> {
        return await axiosInstance.post('user/rotate-password', dto);
    }
};