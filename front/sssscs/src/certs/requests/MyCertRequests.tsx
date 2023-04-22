import { useEffect } from "react";
import { CertRequestDTO, CertRequestService } from "./CertRequestService";
import React from "react";
import { AxiosResponse } from "axios";
import { CertRequestTable } from "./CertRequestTable";

export const MyCertRequests = () => {
    let [reqList, setReqList] = React.useState<Array<CertRequestDTO>>([]);

    useEffect(() => {
        CertRequestService.getOwn()
            .then((res: AxiosResponse<Array<CertRequestDTO>>) => {
                setReqList(res.data);
            });
    }, []);

    return (
        <>
            <CertRequestTable requests={reqList} showColumnStatus={true} />
        </>
    );
}