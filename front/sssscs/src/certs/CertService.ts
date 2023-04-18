import { AxiosResponse } from "axios";
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

export interface CertSummary {
    id: number,
    validFrom: Date,
    subjectData: SubjectData,
    type: CertType,
}


export class CertService {
    static async fetchAllSummary(): Promise<AxiosResponse<Array<CertSummary>>> {
        return await axiosInstance.get('cert');
    }
}