import { Box, Button, Container, Grid } from "@mui/material";
import { useEffect, useState } from "react";
import FormInput from "../../../sharedComponents/FormInput";
import { TypeAllowed, getFirmware, getTypeAllowed, postCreateFirmware, updateFirmware } from "../../../conf/FirmwareController";
import SelectItemsList, { KeyValueItem } from "../../../sharedComponents/SelectItemsList";
import LoadingPage from "../../../sharedComponents/LoadingPage";

export type CreateFirmwareFormData = {
    version: string,
    url: string,
    constructor: string,
    typesAllowed: Set<TypeAllowed>
}

export default function CreateFirmware(props: {id?: number, data?: CreateFirmwareFormData}) {
    const [formData, setFormData] = useState<CreateFirmwareFormData>(props.data ?? {
        version: "",
        url: "",
        constructor: "",
        typesAllowed: new Set<TypeAllowed>()
    });
    const [typeAllowedList, setTypeAllowedList] = useState<KeyValueItem<TypeAllowed>[]>([]);
    const [selectedItems, setSelectedItems] = useState<KeyValueItem<TypeAllowed>[]>([]);
    const [loaded, setLoaded] = useState(false);


    // Fetch the firmware
    useEffect(() => {
        if(!props.id){
            setLoaded(true)
            return
        }
        getFirmware(props.id).then(result => {
            if(!result){
                console.log("Erreur lors de la récupération du firmware.")
                return
            }
            setFormData({
                constructor: result.constructor,
                typesAllowed: result.typesAllowed,
                url: result.url,
                version: result.version
            })
            // Clear selected items
            setSelectedItems([])
            result.typesAllowed.forEach(item => {
                let keyValueItem: KeyValueItem<TypeAllowed> = {
                    item: item,
                    label: item.constructor + "-" + item.type,
                    value: "",
                    id: item.id + "",
                    checker: inputValue => inputValue !== ""
                }
                setSelectedItems(prevSelectedItems => {
                    return [...prevSelectedItems, keyValueItem]
                })
            })
            setLoaded(true)
        });
    }, [props.id])



    // Fetch first charge point page on component load
    useEffect(() => {
        getTypeAllowed().then((result: TypeAllowed[] | undefined) => {
            if(!result){
                return
            }
            setTypeAllowedList(result.map(v => {
                return {
                    item: v,
                    label: v.constructor + "-" + v.type,
                    value: "",
                    id: v.id + "",
                    checker: inputValue => inputValue !== ""
                }
            }))
        });
        }, [])

    return (
        <Box>
            {loaded && (
                <Container maxWidth="xl" sx={{mt: 4, mb: 4}}>
                <Grid container spacing={15}>
                    <Grid item xs={12} md={6}>
                        <Box>
                            <FormInput name={"Version"}
                                    onChange={val => setFormData(prevState => {
                                        return {...prevState, version: val}
                                    })}
                                    checkIsWrong={value => value === "abc"}
                                    value={formData.version}
                            />
                            <FormInput name={"URL"}
                                    onChange={val => setFormData(prevState => {
                                        return {...prevState, url: val}
                                    })}
                                    checkIsWrong={value => value === "abc"}
                                    value={formData.url}
                            />
                            <FormInput name={"Constructeur"}
                                    onChange={val => setFormData(prevState => {
                                        return {...prevState, constructor: val}
                                    })}
                                    checkIsWrong={value => value === "abc"}
                                    value={formData.constructor}
                            />
                        </Box>
                        <Box
                            sx={{
                                display: 'flex',
                                justifyContent: 'center',
                                boxSizing: 'border-box',
                            }}
                            pt={2}
                        >
                            <Button sx={{borderRadius: 28}} variant="contained" color="primary"
                                    onClick={() => {
                                        let typesAllowed = new Set<TypeAllowed>()
                                        selectedItems.forEach(item => {
                                            typesAllowed.add(item.item)
                                        })
                                        let firmware: CreateFirmwareFormData = {
                                            constructor: formData.constructor,
                                            url: formData.url,
                                            typesAllowed: typesAllowed,
                                            version: formData.version,
                                        }
                                        // If id is defined then it's a firmware update
                                        if(props.id){
                                            updateFirmware(props.id, firmware)
                                            return
                                        }
                                        // Otherwise it's a firmware creation
                                        postCreateFirmware(firmware)
                                    }
                                    }>Valider</Button>
                        </Box>

                    </Grid>
                    <Grid item xs={12} md={6}>
                        <SelectItemsList 
                            title={"Modèles compatibles"}
                            keyTitle={"Modèles"}
                            items={typeAllowedList} 
                            selectKind="values"
                            setSelectedItems={setSelectedItems}
                            selectedItems={selectedItems}
                        />
                    </Grid>
                </Grid>
            </Container>
            )}
            {!loaded && (<LoadingPage/>)}
        </Box>

    );
}


