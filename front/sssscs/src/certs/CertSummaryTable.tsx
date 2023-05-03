import { TableContainer, Paper, Table, TableHead, TableRow, TableCell, TableBody } from "@mui/material";
import { CertificateSummaryDTO, subjectToCellStr } from "./CertService";

export interface CertSummaryTableProps {
    summaries: CertificateSummaryDTO[],
    onClickRow?: (row: number) => void,
}

export const CertSummaryTable = (props: CertSummaryTableProps) => {
    return (
        <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Identification Number</TableCell>
                            <TableCell>Not Before</TableCell>
                            <TableCell>Issued To</TableCell>
                            <TableCell>Type</TableCell>
                            <TableCell>Status</TableCell>
                            <TableCell>Revocation reason</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {props.summaries.map((value: CertificateSummaryDTO, index: number) => (
                        <TableRow onClick={() => props.onClickRow && props.onClickRow(index)}>
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
                            <TableCell>
                                {value.status}
                            </TableCell>
                            <TableCell>
                                {value.revocationReason}
                            </TableCell>
                        </TableRow>))}
                    </TableBody>
                </Table>
            </TableContainer>
    )
}