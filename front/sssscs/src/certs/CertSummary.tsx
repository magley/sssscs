import { useEffect } from "react"
import { AuthService } from "../auth/AuthService"
import { CertService, CertificateSummaryDTO, SubjectData, subjectToCellStr } from "./CertService";
import * as React from 'react';
import { AxiosResponse } from "axios";
import { Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow } from "@mui/material";

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
            <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Identification Number</TableCell>
                            <TableCell>Not Before</TableCell>
                            <TableCell>Issued To</TableCell>
                            <TableCell>Type</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {certList.map((value: CertificateSummaryDTO) => (<TableRow>
                            <TableCell>
                                {value.id}
                            </TableCell>
                            <TableCell>
                                {new Date(value.validFrom).toISOString()}
                            </TableCell>
                            <TableCell>
                                {subjectToCellStr(value.subjectData)}
                            </TableCell>
                            <TableCell>
                                {value.type}
                            </TableCell>
                        </TableRow>))}
                    </TableBody>
                </Table>
            </TableContainer>
        </div>
        </>
    )
}