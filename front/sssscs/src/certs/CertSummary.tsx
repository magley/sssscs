import { useEffect } from "react"
import { CertService, CertificateSummaryDTO } from "./CertService";
import * as React from 'react';
import { AxiosResponse } from "axios";
import { CertSummaryTable } from "./CertSummaryTable";



export const CertSummary = () => {
    let [certList, setCertList] = React.useState<Array<CertificateSummaryDTO>>([]);

    useEffect(() => {
        CertService.fetchAllSummary()
            .then((res: AxiosResponse<Array<CertificateSummaryDTO>>) => {
                setCertList(res.data);
            });
    }, []);

    return (
        <>
        <h2>All Certificates</h2>
        <hr/>

        <div style={{ height: 400, width: '100%' }}>
            <CertSummaryTable summaries={certList} />    
        </div>
        </>
    )
}