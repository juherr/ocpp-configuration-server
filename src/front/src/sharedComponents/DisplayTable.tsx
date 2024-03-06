import { Box, Grid, Skeleton, TextField, Typography } from "@mui/material";
import InfiniteScroll from "react-infinite-scroll-component";

export type TableColumnFilterDefinition = {
    apiField: string,
    onChange: (newValue: string) => void,
}

export type TableColumnDefinition = {
    title: string,
    size?: number,
    filter?: TableColumnFilterDefinition,
}

export type PageRequest<T> = {
    total: number,
    page: number,
    size: number,
    data: T[],
    next: string
}

export type InfinityScrollItemsTableProps<T> = {
    key: string, // table key
    columns: TableColumnDefinition[], // table columns definition
    data: T[], // items displayed in the table
    hasMore: boolean, // boolean that check if there is no more element
    error: string | undefined,
    onSelection: (item: T) => void, // function call when an element is selected in the list
    formatter: (item: T, index: number) => JSX.Element // function that transform an item to a JSX element
    fetchData: () => void, // function that fetch next items
}


export function InfinityScrollItemsTable<T>(props: InfinityScrollItemsTableProps<T>) {
    return (
        <Box maxWidth={"true"} paddingTop={2} marginLeft={2}>
            <Box marginRight={2}>
                {/*Display table columns*/}
                <Grid key={"table-header-columns"} container flexDirection={"row"} maxWidth={"true"} >
                    {props.columns.map(column => {
                        return (
                            <Grid xs={column?.size ?? 12/props.columns.length} item key={"table-header-column-" + column.title} justifyContent={"center"}>
                                <Typography variant="h6" textAlign={"center"}>{column.title}</Typography>
                                {column.filter && (
                                    <Grid container maxWidth={"true"} justifyContent={"center"}>
                                        <TextField placeholder={column.title} size="small">
                                        </TextField>
                                    </Grid>
                                )}
                            </Grid>
                        )
                    })}
                </Grid>
            </Box>
             {/*Display table content*/}
            <Box key={"box-items-scrollable-list"} maxWidth={"true"} marginRight={2} marginLeft={2}>
                <div id={"scrollableDiv"}
                key={props.key + "-scrollableDiv-list"}
                style={{
                    height: "75vh",
                    overflow: 'auto',
                }}>
                    <InfiniteScroll
                            key="scrollable-items-list"
                            style={{overflow:"hidden", border: 1, borderColor: "black", maxWidth: "true"}}
                            dataLength={props.data.length}
                            next={() => props.fetchData()}
                            hasMore={props.hasMore}
                            loader={
                                <>
                                    {(props.error !== undefined) && (
                                        <Typography variant="h6" color={"red"} textAlign={"center"}>{props.error}</Typography>
                                    )}
                                    {(props.error === undefined) && (
                                        <Box key={"box-skeleton-list"} maxWidth={"true"} marginRight={2} marginLeft={2}>
                                            <Box display="flex" flexDirection="column">
                                                {Array.from(Array(30).keys()).map((_, index) => (
                                                    <Box key={"skeleton-list-" + index} marginY={1}>
                                                        <Skeleton sx={{borderRadius: 50}} variant="rectangular" width={"100%"} height={"5vh"} />
                                                    </Box>
                                                ))}
                                            </Box>
                                        </Box>
                                    )}
                                </>
                            }
                            scrollableTarget={"scrollableDiv"}
                        >
                        {props.data.map((item: T, index) => props.formatter(item, index))}
                    </InfiniteScroll>
                </div>
            </Box>
        </Box>
    );
}