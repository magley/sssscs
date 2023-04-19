import { AxiosResponse } from "axios";
import { axiosInstance } from "../../http/HttpService";
import { CertType, CertificateSummaryDTO, SubjectData } from "../CertService";
import { Subject } from "react-hook-form/dist/utils/createSubject";

export enum CertRequestStatus {
    PENDING,
	ACCEPTED,
	REJECTED,
}

export interface CertRequestDTO {
    id: number,
    subjectData: SubjectData,
    validTo: Date,
    parentId: number,
    type: CertType,
    status: CertRequestStatus,
    rejectionReason: string | undefined,
}

export interface CertRequestCreateDTO {
    type: string,
    validTo: Date,
    parentId: number | null,
    subjectData: SubjectData,
}

export class CertRequestService {
    static async getOwn(): Promise<AxiosResponse<Array<CertRequestDTO>>> {
        return await axiosInstance.get(`cert/request/created`);
    }

    static async getIssuedToMe(): Promise<AxiosResponse<Array<CertRequestDTO>>> {
        return await axiosInstance.get(`cert/request/incoming`);
    }

    static async accept(id: number): Promise<AxiosResponse<void>> {
        return await axiosInstance.put(`cert/request/accept/${id}`);
    }

    static async reject(id: number, reason: string): Promise<AxiosResponse<void>> {
        return await axiosInstance.put(`cert/request/reject/${id}`, reason, {headers: {"Content-Type": "text/plain"}});
    }

    static async create(payload: CertRequestCreateDTO): Promise<AxiosResponse<void>> {
        return await axiosInstance.post(`cert/request`, payload);
    }
}