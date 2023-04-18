import { AxiosResponse } from "axios";
import { axiosInstance } from "../../http/HttpService";
import { CertType, CertificateSummaryDTO, SubjectData } from "../CertService";

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

export class CertRequestService {
    static async getOwn(): Promise<AxiosResponse<Array<CertRequestDTO>>> {
        return await axiosInstance.get(`cert/request/created`);
    }
}