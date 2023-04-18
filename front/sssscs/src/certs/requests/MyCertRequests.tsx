import { useEffect } from "react";
import { CertRequestDTO, CertRequestService } from "./CertRequestService";
import React from "react";
import { AxiosResponse } from "axios";

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
            <ul>
                {
                    reqList.map((item: CertRequestDTO) => (
                        <li>{JSON.stringify(item)}</li>
                    ))
                }
            </ul>
        </>
    );
}