// Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
// or more contributor license agreements. Licensed under the Elastic License
// 2.0; you may not use this file except in compliance with the Elastic License
// 2.0.
package org.elasticsearch.compute.aggregation;

import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.util.List;
import org.apache.lucene.util.BytesRef;
import org.elasticsearch.common.util.BigArrays;
import org.elasticsearch.compute.data.Block;
import org.elasticsearch.compute.data.BytesRefBlock;
import org.elasticsearch.compute.data.BytesRefVector;
import org.elasticsearch.compute.data.ElementType;
import org.elasticsearch.compute.data.IntVector;
import org.elasticsearch.compute.data.LongBlock;
import org.elasticsearch.compute.data.LongVector;
import org.elasticsearch.compute.data.Page;

/**
 * {@link GroupingAggregatorFunction} implementation for {@link PercentileLongAggregator}.
 * This class is generated. Do not edit it.
 */
public final class PercentileLongGroupingAggregatorFunction implements GroupingAggregatorFunction {
  private static final List<IntermediateStateDesc> INTERMEDIATE_STATE_DESC = List.of(
      new IntermediateStateDesc("quart", ElementType.BYTES_REF)  );

  private final QuantileStates.GroupingState state;

  private final List<Integer> channels;

  private final BigArrays bigArrays;

  private final double percentile;

  public PercentileLongGroupingAggregatorFunction(List<Integer> channels,
      QuantileStates.GroupingState state, BigArrays bigArrays, double percentile) {
    this.channels = channels;
    this.state = state;
    this.bigArrays = bigArrays;
    this.percentile = percentile;
  }

  public static PercentileLongGroupingAggregatorFunction create(List<Integer> channels,
      BigArrays bigArrays, double percentile) {
    return new PercentileLongGroupingAggregatorFunction(channels, PercentileLongAggregator.initGrouping(bigArrays, percentile), bigArrays, percentile);
  }

  public static List<IntermediateStateDesc> intermediateStateDesc() {
    return INTERMEDIATE_STATE_DESC;
  }

  @Override
  public int intermediateBlockCount() {
    return INTERMEDIATE_STATE_DESC.size();
  }

  @Override
  public GroupingAggregatorFunction.AddInput prepareProcessPage(Page page) {
    Block uncastValuesBlock = page.getBlock(channels.get(0));
    if (uncastValuesBlock.areAllValuesNull()) {
      return new GroupingAggregatorFunction.AddInput() {
        @Override
        public void add(int positionOffset, LongBlock groupIds) {
          addRawInputAllNulls(positionOffset, groupIds, uncastValuesBlock);
        }

        @Override
        public void add(int positionOffset, LongVector groupIds) {
          addRawInputAllNulls(positionOffset, groupIds, uncastValuesBlock);
        }
      };
    }
    LongBlock valuesBlock = (LongBlock) uncastValuesBlock;
    LongVector valuesVector = valuesBlock.asVector();
    if (valuesVector == null) {
      return new GroupingAggregatorFunction.AddInput() {
        @Override
        public void add(int positionOffset, LongBlock groupIds) {
          addRawInput(positionOffset, groupIds, valuesBlock);
        }

        @Override
        public void add(int positionOffset, LongVector groupIds) {
          addRawInput(positionOffset, groupIds, valuesBlock);
        }
      };
    }
    return new GroupingAggregatorFunction.AddInput() {
      @Override
      public void add(int positionOffset, LongBlock groupIds) {
        addRawInput(positionOffset, groupIds, valuesVector);
      }

      @Override
      public void add(int positionOffset, LongVector groupIds) {
        addRawInput(positionOffset, groupIds, valuesVector);
      }
    };
  }

  private void addRawInput(int positionOffset, LongVector groups, LongBlock values) {
    for (int groupPosition = 0; groupPosition < groups.getPositionCount(); groupPosition++) {
      int groupId = Math.toIntExact(groups.getLong(groupPosition));
      if (values.isNull(groupPosition + positionOffset)) {
        state.putNull(groupId);
        continue;
      }
      int valuesStart = values.getFirstValueIndex(groupPosition + positionOffset);
      int valuesEnd = valuesStart + values.getValueCount(groupPosition + positionOffset);
      for (int v = valuesStart; v < valuesEnd; v++) {
        PercentileLongAggregator.combine(state, groupId, values.getLong(v));
      }
    }
  }

  private void addRawInput(int positionOffset, LongVector groups, LongVector values) {
    for (int groupPosition = 0; groupPosition < groups.getPositionCount(); groupPosition++) {
      int groupId = Math.toIntExact(groups.getLong(groupPosition));
      PercentileLongAggregator.combine(state, groupId, values.getLong(groupPosition + positionOffset));
    }
  }

  private void addRawInputAllNulls(int positionOffset, LongVector groups, Block values) {
    for (int groupPosition = 0; groupPosition < groups.getPositionCount(); groupPosition++) {
      int groupId = Math.toIntExact(groups.getLong(groupPosition));
      assert values.isNull(groupPosition + positionOffset);
      state.putNull(groupPosition + positionOffset);
    }
  }

  private void addRawInput(int positionOffset, LongBlock groups, LongBlock values) {
    for (int groupPosition = 0; groupPosition < groups.getPositionCount(); groupPosition++) {
      if (groups.isNull(groupPosition)) {
        continue;
      }
      int groupStart = groups.getFirstValueIndex(groupPosition);
      int groupEnd = groupStart + groups.getValueCount(groupPosition);
      for (int g = groupStart; g < groupEnd; g++) {
        int groupId = Math.toIntExact(groups.getLong(g));
        if (values.isNull(groupPosition + positionOffset)) {
          state.putNull(groupId);
          continue;
        }
        int valuesStart = values.getFirstValueIndex(groupPosition + positionOffset);
        int valuesEnd = valuesStart + values.getValueCount(groupPosition + positionOffset);
        for (int v = valuesStart; v < valuesEnd; v++) {
          PercentileLongAggregator.combine(state, groupId, values.getLong(v));
        }
      }
    }
  }

  private void addRawInput(int positionOffset, LongBlock groups, LongVector values) {
    for (int groupPosition = 0; groupPosition < groups.getPositionCount(); groupPosition++) {
      if (groups.isNull(groupPosition)) {
        continue;
      }
      int groupStart = groups.getFirstValueIndex(groupPosition);
      int groupEnd = groupStart + groups.getValueCount(groupPosition);
      for (int g = groupStart; g < groupEnd; g++) {
        int groupId = Math.toIntExact(groups.getLong(g));
        PercentileLongAggregator.combine(state, groupId, values.getLong(groupPosition + positionOffset));
      }
    }
  }

  private void addRawInputAllNulls(int positionOffset, LongBlock groups, Block values) {
    for (int groupPosition = 0; groupPosition < groups.getPositionCount(); groupPosition++) {
      if (groups.isNull(groupPosition)) {
        continue;
      }
      int groupStart = groups.getFirstValueIndex(groupPosition);
      int groupEnd = groupStart + groups.getValueCount(groupPosition);
      for (int g = groupStart; g < groupEnd; g++) {
        int groupId = Math.toIntExact(groups.getLong(g));
        assert values.isNull(groupPosition + positionOffset);
        state.putNull(groupPosition + positionOffset);
      }
    }
  }

  @Override
  public void addIntermediateInput(LongVector groupIdVector, Page page) {
    assert channels.size() == intermediateBlockCount();
    assert page.getBlockCount() >= channels.get(0) + intermediateStateDesc().size();
    BytesRefVector quart = page.<BytesRefBlock>getBlock(channels.get(0)).asVector();
    BytesRef scratch = new BytesRef();
    for (int position = 0; position < groupIdVector.getPositionCount(); position++) {
      int groupId = Math.toIntExact(groupIdVector.getLong(position));
      PercentileLongAggregator.combineIntermediate(state, groupId, quart.getBytesRef(position, scratch));
    }
  }

  @Override
  public void addIntermediateRowInput(int groupId, GroupingAggregatorFunction input, int position) {
    if (input.getClass() != getClass()) {
      throw new IllegalArgumentException("expected " + getClass() + "; got " + input.getClass());
    }
    QuantileStates.GroupingState inState = ((PercentileLongGroupingAggregatorFunction) input).state;
    PercentileLongAggregator.combineStates(state, groupId, inState, position);
  }

  @Override
  public void evaluateIntermediate(Block[] blocks, int offset, IntVector selected) {
    state.toIntermediate(blocks, offset, selected);
  }

  @Override
  public void evaluateFinal(Block[] blocks, int offset, IntVector selected) {
    blocks[offset] = PercentileLongAggregator.evaluateFinal(state, selected);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getClass().getSimpleName()).append("[");
    sb.append("channels=").append(channels);
    sb.append("]");
    return sb.toString();
  }

  @Override
  public void close() {
    state.close();
  }
}
