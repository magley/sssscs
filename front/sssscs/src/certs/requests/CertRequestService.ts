import { AxiosResponse } from "axios";
import { axiosInstance } from "../../http/HttpService";
import { CertType, SubjectData } from "../CertService";

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
    parentId: number | null,
    subjectData: SubjectData,
}

export class CertRequestService {
    static async getOwn(): Promise<AxiosResponse<Array<CertRequestDTO>>> {
        return await axiosInstance.get(`cert/request/created`);
    }

    static async getAll(): Promise<AxiosResponse<Array<CertRequestDTO>>> {
        return await axiosInstance.get(`cert/request/all`);
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

    static async revoke(certificateId: number, revocationReason: string): Promise<AxiosResponse<void>> {
        return await axiosInstance.put(`cert/revoke/${certificateId}`, revocationReason, {headers: {"Content-Type": "text/plain"}})
    }
}