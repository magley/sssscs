import axios, { AxiosResponse } from "axios";
import { Env } from "../common/Environment";
import { AuthService } from "../auth/AuthService";

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
        const config = {
            headers: { Authorization: `Bearer ${AuthService.getJWTString()}` }
        };
        
        
        return await axios.get(`${Env.url}/api/cert`, config);
    }
}