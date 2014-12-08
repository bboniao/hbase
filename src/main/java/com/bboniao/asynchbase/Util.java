package com.bboniao.asynchbase;

import com.google.protobuf.HBaseZeroCopyByteString;
import org.apache.hadoop.hbase.protobuf.generated.CellProtos;
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos;
import org.apache.hadoop.hbase.protobuf.generated.HBaseProtos;

import java.io.IOException;
import java.util.*;

/**
 *
 * Created by bboniao on 11/19/14.
 */
public final class Util {

    private static ClientProtos.Get toGet(
            final GetReq get) throws IOException {
        ClientProtos.Get.Builder builder =
                ClientProtos.Get.newBuilder();
        builder.setRow(HBaseZeroCopyByteString.wrap(get.getRow()));
        builder.setCacheBlocks(get.getCacheBlocks());
        builder.setMaxVersions(get.getMaxVersions());
        if (get.hasFamilies()) {
            ClientProtos.Column.Builder columnBuilder = ClientProtos.Column.newBuilder();
            Map<byte[], NavigableSet<byte[]>> families = get.getFamilyMap();
            for (Map.Entry<byte[], NavigableSet<byte[]>> family: families.entrySet()) {
                NavigableSet<byte[]> qualifiers = family.getValue();
                columnBuilder.setFamily(HBaseZeroCopyByteString.wrap(family.getKey()));
                columnBuilder.clearQualifier();
                if (qualifiers != null && qualifiers.size() > 0) {
                    for (byte[] qualifier: qualifiers) {
                        columnBuilder.addQualifier(HBaseZeroCopyByteString.wrap(qualifier));
                    }
                }
                builder.addColumn(columnBuilder.build());
            }
        }
        return builder.build();
    }

    public static ClientProtos.GetRequest buildGetRequest(final byte[] regionName,
                                                          final GetReq get) throws IOException {
        ClientProtos.GetRequest.Builder builder = ClientProtos.GetRequest.newBuilder();
        HBaseProtos.RegionSpecifier region = buildRegionSpecifier(
                HBaseProtos.RegionSpecifier.RegionSpecifierType.REGION_NAME, regionName);
        builder.setRegion(region);
        builder.setGet(toGet(get));
        return builder.build();
    }

    private static HBaseProtos.RegionSpecifier buildRegionSpecifier(
            final HBaseProtos.RegionSpecifier.RegionSpecifierType type, final byte[] value) {
        HBaseProtos.RegionSpecifier.Builder regionBuilder = HBaseProtos.RegionSpecifier.newBuilder();
        regionBuilder.setValue(HBaseZeroCopyByteString.wrap(value));
        regionBuilder.setType(type);
        return regionBuilder.build();
    }

    public static List<KV> toResult(final ClientProtos.Result proto) {

        List<CellProtos.Cell> values = proto.getCellList();
        if (values.isEmpty()){
            return Collections.emptyList();
        }

        List<KV> cells = new ArrayList<KV>(values.size());
        for (CellProtos.Cell c : values) {
            cells.add(toCell(c));
        }
        return cells;
    }

    private static KV toCell(final CellProtos.Cell cell) {
        // Doing this is going to kill us if we do it for all data passed.
        // St.Ack 20121205
        return new KV(cell.getRow().toByteArray(),
                cell.getFamily().toByteArray(),
                cell.getQualifier().toByteArray(),
                cell.getTimestamp(),
                (byte) cell.getCellType().getNumber(),
                cell.getValue().toByteArray());
    }
}
