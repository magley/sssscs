export enum VerificationMethod {
    EMAIL, SMS
};

export interface VerificationCodeSendRequestDTO {
    userEmail: string,
    method: VerificationMethod,
    dontActuallySend: boolean
};

export interface VerifyPageRouterState {
    email: string
}