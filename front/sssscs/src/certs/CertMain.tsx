import { useEffect } from "react"
import { AuthService } from "../auth/AuthService"
import { CertService, CertSummary } from "./CertService";
import * as React from 'react';
import { AxiosResponse } from "axios";

export const CertMain = () => {
    let [certList, setCertList] = React.useState<Array<CertSummary>>([]);


    useEffect(() => {
        CertService.fetchAllSummary()
            .then((res: AxiosResponse<Array<CertSummary>>) => {
                setCertList(res.data);
            });
    }, []);

    return (
        <>
        <b>You can only see this if you're logged in.</b>
        <br/>
        <p>{AuthService.getEmail()}</p>
        <hr/>

        <ul>
            {
                certList.map(cert => 
                    <li>{JSON.stringify(cert)}</li>
                )
            }
        </ul>
        </>
    )
}