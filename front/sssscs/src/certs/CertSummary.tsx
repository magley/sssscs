import { useEffect, useState } from "react"
import { CertService, CertificateSummaryDTO, CertStatus } from "./CertService";
import { AxiosError, AxiosResponse } from "axios";
import { CertSummaryTable } from "./CertSummaryTable";
import { CertRequestService } from "./requests/CertRequestService";
import { Button, Box, TextField } from "@mui/material";
import { FieldValues, useForm } from "react-hook-form";



export const CertSummary = () => {
    let [certList, setCertList] = useState<Array<CertificateSummaryDTO>>([]);
    let [selectedCert, setSelectedCert] = useState<CertificateSummaryDTO | null>(null);
    let { register, handleSubmit, formState: {errors }} = useForm({mode: 'all'});
    let [ revocationError, setRevocationError ] = useState('');

    useEffect(() => {
        updateSummary();
    }, []);

    const updateSummary = () => {
        CertService.fetchAllSummary()
            .then((res: AxiosResponse<Array<CertificateSummaryDTO>>) => {
                setCertList(res.data);
            });
    }

    const onClickItem = (index: number) => {
        const selectedItem = certList[index];
        setSelectedCert(selectedItem);
    }

    const revokeSelectedCert = async (data: FieldValues) => {
        if (selectedCert == null) {
            return;
        }
        CertRequestService.revoke(selectedCert.id, data['revocationReason'])
            .then((res: AxiosResponse<void>) => {
                updateSummary();
                setSelectedCert(null);
                setRevocationError('');
            })
            .catch((err: AxiosError) => {
                console.error(err.response?.data);
                console.error(err.response?.status);
                if (err.response?.status === 403) {
                    setRevocationError('User cannot revoke this certificate.')
                }
            });
    }

    return (
        <>
        <h2>All Certificates</h2>
        <hr/>
        <CertSummaryTable summaries={certList} onClickRow={onClickItem} />
        {/* TODO: I hate typescript enums so much. Is there a nicer way to do this? */}
        { selectedCert && selectedCert.status.toString() !== CertStatus[CertStatus.REVOKED].valueOf() && (
                <>
                Certificate with ID: <b>{selectedCert.id}</b>
                <br/>
                <Box component='form' noValidate onSubmit={handleSubmit(revokeSelectedCert)}>
                    <TextField 
                        label='Revocation reason' 
                        required 
                        type='text' 
                        {...register('revocationReason', { required: 'Revocation reason is required' })}
                        error={!!errors['revocationReason']}
                        helperText={errors['revocationReason']?.message?.toString()}
                    />
                    <br/>
                    <Button variant='contained' type='submit'>Revoke</Button>
                </Box>
                { revocationError && (
                    <p>{revocationError}</p>
                )}
            </>
        )}
        </>
    )
}