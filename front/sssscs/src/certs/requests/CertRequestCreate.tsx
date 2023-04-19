import { Box, Button, InputLabel, MenuItem, Select, TextField, Typography } from "@mui/material";
import { FieldValues, useForm } from "react-hook-form";
import { CertType } from "../CertService";
import { useEffect, useState } from "react";
import { AuthService } from "../../auth/AuthService";
import { CertRequestCreateDTO, CertRequestService } from "./CertRequestService";


export const CertRequestCreate = () => {
    const { register, control, handleSubmit, formState: { errors }, watch} = useForm({mode: 'all'});
    const [ allowedTypes, setAllowedTypes ] = useState<Array<CertType>>([]);

    useEffect(() => {
        let t = [CertType.END, CertType.INTERMEDIATE, CertType.ROOT];
        if (AuthService.getRole() != 'ADMIN') {
            t.pop();
        }

        setAllowedTypes(t);
    }, []);

    const sendRequest = async (data: FieldValues) => {
        const validToAttempt = new Date(Date.parse(data['validTo']));
        // TODO: Datetime validation.
        // Raw strings are dumb, but the MUI date picker doesn't work well with
        // hook forms.....

        const dto: CertRequestCreateDTO = {
            type: CertType[data['type']],
            parentId: Number(data['parentId']),
            validTo: validToAttempt,
            subjectData: {
                name: data['subjectName'],
                surname: data['subjectSurname'],
                email: data['subjectEmail'],
                organization: data['subjectOrganization'],
                commonName: data['subjectCommonName'],
            },
        };

        if (dto.type != CertType[CertType.ROOT]) {
            if (dto.parentId == null || dto.parentId <= 0) {
                console.error("Need parent!");
                return;
            }
        } else {
            dto.parentId = null;
        }
          
        CertRequestService.create(dto)
            .then((v) => {
                console.log(v.data);
                console.log(v.status);
            })
            .catch((err) => {
                console.error(err.response.data);
            })
    }

    return (
        <>
            <Box component='form' noValidate onSubmit={handleSubmit(sendRequest)}>
                <Typography variant='h4'>Create new certificate</Typography>

                <InputLabel id="cert-type">Type</InputLabel>
                <Select
                    labelId="cert-type"
                    defaultValue={CertType.END}
                    required
                    {...register('type', { required: 'Type is required' })}
                    error={!!errors['type']}
                    >
                        { allowedTypes.map((t: CertType) => (
                            <MenuItem value={t}>{CertType[t]}</MenuItem>
                        ))}
                </Select>
                <br/>
                    
                <TextField
                    label="Parent certificate ID"
                    disabled={watch("type") == CertType.ROOT}
                    required={watch("type") != CertType.ROOT}
                    defaultValue={0}
                    value={watch("type") == CertType.ROOT ? 0 : watch('parentId')}
                    {...register('parentId', { required: 'Parent ID is required' })}
                    error={!!errors['parentId']}
                    helperText={errors['parentId']?.message?.toString()}
                    />
                <br/>

                <TextField
                    label="Not after"
                    required
                    {...register('validTo', {required: "Date requred"})}
                    error={!!errors['validTo']}
                    helperText={errors['validTo']?.message?.toString()}
                    />

                <InputLabel>Subject</InputLabel>
                <Box>
                    <TextField 
                        label="Common Name"
                        required
                        {...register('subjectCommonName', {required: "Subject's common name is required"})}
                        error={!!errors['subjectCommonName']}
                        helperText={errors['subjectCommonName']?.message?.toString()}
                        />
                    <TextField 
                        label="Name"
                        {...register('subjectName')}
                        />
                     <TextField 
                        label="Surname"
                        {...register('subjectSurname')}
                        />
                    <TextField 
                        label="Email"
                        type="email"
                        {...register('subjectEmail')}
                        />
                        <TextField 
                        label="Organization"
                        {...register('subjectOrganization')}
                        />
                </Box>

                <Button variant="contained" type="submit">
                    Send Request
                </Button>
            </Box>
        </>
    );
}