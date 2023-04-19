import { AxiosResponse } from "axios";
import React, { useEffect } from "react";
import { CertRequestDTO, CertRequestService } from "./CertRequestService";
import { Box, Button, TextField } from "@mui/material";
import { FieldValues, useForm } from "react-hook-form";

export const CertRequestsIssuedToMe = () => {
    let [reqList, setReqList] = React.useState<Array<CertRequestDTO>>([]);
    let [selectedReq, setSelectedReq] = React.useState<CertRequestDTO | null>(null);
    let { register, handleSubmit, formState: {errors }} = useForm({mode: 'all'});

    useEffect(() => {
        CertRequestService.getIssuedToMe()
            .then((res: AxiosResponse<Array<CertRequestDTO>>) => {
                setReqList(res.data);
            });
    }, []);
    
    const onClickItem = (index: number) => {
        const selectedItem = reqList[index];
        setSelectedReq(selectedItem);
    }

    const acceptSelectedReq = async () => {
        if (selectedReq == null) {
            return;
        }
        CertRequestService.accept(selectedReq.id)
            .then((res: AxiosResponse<void>) => {
                
            });
    }

    const rejectSelectedReq = async (data: FieldValues) => {
        if (selectedReq == null) {
            return;
        }
        CertRequestService.reject(selectedReq.id, data['reason'])
            .then((res: AxiosResponse<void>) => {

            });
    }

    return (
        <>
            <ul>
                {
                    reqList.map((item: CertRequestDTO, index: number) => (
                        <li onClick={() => onClickItem(index)}>{JSON.stringify(item)}</li>
                    ))
                }
            </ul>

            { selectedReq && (
                <>
                    Request with ID: <b>{selectedReq.id}</b>
                    <br/>
                    <Button variant='contained' onClick={() => acceptSelectedReq()}>Accept</Button>
                    <br/>
                    <br/>
                    <Box component='form' noValidate onSubmit={handleSubmit(rejectSelectedReq)}>
                        <TextField 
                            label='Reason' 
                            required 
                            type='text' 
                            {...register('reason', { required: 'Reason is required' })}
                            error={!!errors['reason']}
                            helperText={errors['reason']?.message?.toString()}
                        />
                        <br/>
                        <Button variant='contained' type='submit'>Reject</Button>
                    </Box>
                </>
            )}
        </>
    )
}