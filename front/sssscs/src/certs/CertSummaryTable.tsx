import { TableContainer, Paper, Table, TableHead, TableRow, TableCell, TableBody, IconButton } from "@mui/material";
import DownloadIcon from '@mui/icons-material/Download';
import KeyIcon from '@mui/icons-material/Key';
import { AuthService } from "./../auth/AuthService"
import { CertService, CertificateSummaryDTO, subjectToCellStr } from "./CertService";
import { AxiosError, AxiosResponse } from "axios";

export interface CertSummaryTableProps {
    summaries: CertificateSummaryDTO[],
    onClickRow?: (row: number) => void,
}

export const CertSummaryTable = (props: CertSummaryTableProps) => {
    const downloadCert = (id: number) => {
        CertService.download(id)
            .then((res: AxiosResponse<ArrayBuffer>) => {
                const url = window.URL.createObjectURL(new Blob([res.data]));
                const link = document.createElement("a");
                link.href = url;
                link.setAttribute("download", id + ".crt");
                link.click();
            })
            .catch((err : AxiosError) => {
                // TODO: maybe need to catch?
            });
    }
    const downloadKey = (id: number) => {
        CertService.downloadPrivateKey(id)
            .then((res: AxiosResponse<ArrayBuffer>) => {
                const url = window.URL.createObjectURL(new Blob([res.data]));
                const link = document.createElement("a");
                link.href = url;
                link.setAttribute("download", id + ".key");
                link.click();
            })
            .catch((err : AxiosError) => {
                // TODO: maybe need to catch?
            });
    }
    return (
        <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Identification Number</TableCell>
                            <TableCell>Not Before</TableCell>
                            <TableCell>Not After</TableCell>
                            <TableCell>Issued To</TableCell>
                            <TableCell>Type</TableCell>
                            <TableCell>Status</TableCell>
                            <TableCell>Revocation reason</TableCell>
                            <TableCell>Download certificate</TableCell>
                            <TableCell>Download key</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {props.summaries.map((value: CertificateSummaryDTO, index: number) => (
                        <TableRow onClick={() => props.onClickRow && props.onClickRow(index)} key={value.id}>
                            <TableCell>
                                {value.id}
                            </TableCell>
                            <TableCell>
                                {new Date(value.validFrom).toISOString()}
                            </TableCell>
                            <TableCell>
                                {new Date(value.validTo).toISOString()}
                            </TableCell>
                            <TableCell>
                                {subjectToCellStr(value.subjectData)}
                            </TableCell>
                            <TableCell>
                                {value.type}
                            </TableCell>
                            <TableCell>
                                {value.status}
                            </TableCell>
                            <TableCell>
                                {value.revocationReason}
                            </TableCell>
                            <TableCell>
                                <IconButton color="primary" onClick={(e) => {downloadCert(value.id); e.stopPropagation();}}>
                                    <DownloadIcon/>
                                </IconButton>
                            </TableCell>
                            <TableCell>
                                {
                                    value.ownerId === AuthService.getId() ?
                                    <IconButton color="primary" onClick={(e) => {downloadKey(value.id); e.stopPropagation();}}>
                                        <KeyIcon/>
                                    </IconButton> : "N/A"
                                }
                            </TableCell>
                        </TableRow>))}
                    </TableBody>
                </Table>
            </TableContainer>
    )
}