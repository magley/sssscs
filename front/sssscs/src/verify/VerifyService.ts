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

export interface VerificationCodeResetDTO {
    userEmail: string,
    code: string,
    newPassword: string,
}

export class VerifyService {
    static async sendCode(dto: VerificationCodeSendRequestDTO): Promise<AxiosResponse<void>> {
        return await axiosInstance.post(`verification-code/send`, dto);
    }

    static async verifyUser(dto: VerificationCodeVerifyDTO): Promise<AxiosResponse<void>> {
        return await axiosInstance.post(`verification-code/verify`, dto);
    }

    static async resetPassword(dto: VerificationCodeResetDTO): Promise<AxiosResponse<void>> {
        return await axiosInstance.post('verification-code/reset-password', dto);
    }
}