import { AxiosResponse } from "axios";
import { axiosInstance } from "../http/HttpService";

export enum CertType {
    ROOT,
	INTERMEDIATE,
	END
}

export enum CertStatus {
    GOOD,
    REVOKED,
    UNKNOWN,
}

export interface SubjectData {
    name: string,
    surname: string,
    email: string,
    organization: string,
    commonName: string,
}

export interface CertificateSummaryDTO {
    id: number,
    validFrom: Date,
    validTo: Date,
    subjectData: SubjectData,
    type: CertType,
    status: CertStatus,
    revocationReason?: String,
}


export class CertService {
    static async fetchAllSummary(): Promise<AxiosResponse<Array<CertificateSummaryDTO>>> {
        return await axiosInstance.get(`cert`);
    }

    static async verify(certID: number): Promise<AxiosResponse<Boolean>> {
        return await axiosInstance.get(`cert/valid/${certID}`);
    }

    static async verifyFile(certFile: File): Promise<AxiosResponse<Boolean>> {
        return await axiosInstance.postForm(`cert/valid`, { certFile });
    }

    static async download(certID: number): Promise<AxiosResponse<ArrayBuffer>> {
        return await axiosInstance.get(`cert/download/${certID}`);
    }
}

export const subjectToCellStr = (s: SubjectData) => {
    let str: string = "";
    if (s.name !== "") {
        str += s.name + " ";
    }
    if (s.surname !== "") {
        str += s.surname + " ";
    }
    if (s.name !== "" || s.surname !== "") {
        if (s.commonName !== "") {
            str += `(${s.commonName}), `;
        }
    } else {
        if (s.commonName !== "") {
            str += `${s.commonName} `;
        }
    }
    if (s.email !== "") {
        str += s.email + ", ";
    }
    if (s.organization !== "") {
        str += s.organization;
    }

    return str;
}