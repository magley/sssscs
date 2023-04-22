import axios, { AxiosResponse } from "axios";
import { axiosInstance } from "../http/HttpService";

export enum CertType {
    ROOT,
	INTERMEDIATE,
	END
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
    subjectData: SubjectData,
    type: CertType,
}


export class CertService {
    static async fetchAllSummary(): Promise<AxiosResponse<Array<CertificateSummaryDTO>>> {
        return await axiosInstance.get(`cert`);
    }

    static async verify(certID: number): Promise<AxiosResponse<Boolean>> {
        return await axiosInstance.get(`cert/valid/${certID}`);
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
    if (s.organization != "") {
        str += s.organization;
    }

    return str;
}