import { useEffect } from "react";
import { CertRequestDTO } from "./CertRequestService";
import React from "react";
import { AxiosResponse } from "axios";
import { CertRequestTable } from "./CertRequestTable";

export const CertRequests = (props: {requestsProvider: () => Promise<AxiosResponse<CertRequestDTO[]>>}) => {
    let [reqList, setReqList] = React.useState<Array<CertRequestDTO>>([]);

    useEffect(() => {
        props.requestsProvider()
            .then((res: AxiosResponse<Array<CertRequestDTO>>) => {
                setReqList(res.data);
            });
    }, [props]);

    return (
        <>
            <CertRequestTable requests={reqList} showColumnStatus={true} />
        </>
    );
}