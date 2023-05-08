import { TableContainer, Paper, Table, TableHead, TableRow, TableCell, TableBody } from "@mui/material"
import { subjectToCellStr } from "../CertService"
import { CertRequestDTO } from "./CertRequestService"

export interface CertRequestTableProps {
    requests: Array<CertRequestDTO>
    onClickRow?: (row:number) => void
    showColumnStatus?: boolean
}

export const CertRequestTable = (props: CertRequestTableProps) => {
    return (
        <>
            <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Identification Number</TableCell>
                            <TableCell>Issued To</TableCell>
                            <TableCell>Not After</TableCell>
                            <TableCell>Parent Certificate ID</TableCell>
                            <TableCell>Type</TableCell>
                            {props.showColumnStatus && <TableCell>Status</TableCell>}
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {props.requests.map((value: CertRequestDTO, index: number) => (
                            <TableRow onClick={() => props.onClickRow && props.onClickRow(index)} key={value.id}>
                                <TableCell>
                                    {value.id}
                                </TableCell>
                                <TableCell>
                                    {subjectToCellStr(value.subjectData)}
                                </TableCell>
                                <TableCell>
                                    {new Date(value.validTo).toISOString()}
                                </TableCell>
                                <TableCell>
                                    {value.parentId}
                                </TableCell>
                                <TableCell>
                                    {value.type}
                                </TableCell>
                                {props.showColumnStatus && <TableCell>
                                    {value.status}    
                                </TableCell>}
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </>
    )
}