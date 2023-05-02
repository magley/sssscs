import { AxiosResponse } from "axios";
import { axiosInstance } from "../http/HttpService";

export enum VerificationMethod {
    EMAIL, SMS,
};

export enum VerificationReason {
    TWO_FA, RESET_PASSWORD,
}

export interface VerificationCodeSendRequestDTO {
    userEmail: string,
    method: VerificationMethod,
    dontActuallySend: boolean,
    reason: VerificationReason,
};

export interface VerifyPageRouterState {
    email: string,
}

export interface VerificationCodeVerifyDTO {
    userEmail: string,
    code: string,
}

export class VerifyService {
    static async sendCode(dto: VerificationCodeSendRequestDTO): Promise<AxiosResponse<void>> {
        return await axiosInstance.post(`verification-code/send`, dto);
    }

    static async verifyUser(dto: VerificationCodeVerifyDTO): Promise<AxiosResponse<void>> {
        return await axiosInstance.post(`verification-code/verify`, dto);
    }
}