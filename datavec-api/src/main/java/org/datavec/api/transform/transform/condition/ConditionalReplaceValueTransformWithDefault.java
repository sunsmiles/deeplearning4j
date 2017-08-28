/*-
 *  * Copyright 2016 Skymind, Inc.
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 */

package org.datavec.api.transform.transform.condition;

import lombok.EqualsAndHashCode;
import org.datavec.api.transform.ColumnOp;
import org.datavec.api.transform.Transform;
import org.datavec.api.transform.condition.Condition;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.writable.Writable;
import org.nd4j.shade.jackson.annotation.JsonIgnoreProperties;
import org.nd4j.shade.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Replace the value in a specified column with a new value, if a condition is satisfied/true.<br>
 * Note that the condition can be any generic condition, including on other column(s), different to the column
 * that will be modified if the condition is satisfied/true.<br>
 * <p>
 * <b>Note</b>: For sequences, this transform use the convention that each step in the sequence is passed to the condition,
 * and replaced (or not) separately (i.e., Condition.condition(List<Writable>) is used on each time step individually)
 *
 * @author Alex Black
 * @author kepricon
 * @see ConditionalReplaceValueTransform the version without a default
 */
@JsonIgnoreProperties({"filterColIdx"})
@EqualsAndHashCode(exclude = {"filterColIdx"})
public class ConditionalReplaceValueTransformWithDefault implements Transform, ColumnOp {


    protected final String columnToReplace;
    protected Writable newVal;
    protected Writable defaultVal;
    protected int filterColIdx;
    protected final Condition condition;

    public ConditionalReplaceValueTransformWithDefault(@JsonProperty("columnToReplace") String columnToReplace,
                                @JsonProperty("newVal") Writable newVal,
                                @JsonProperty("defaultVal") Writable defaultVal,
                                @JsonProperty("condiiton") Condition condition) {
        this.columnToReplace = columnToReplace;
        this.newVal = newVal;
        this.defaultVal = defaultVal;
        this.condition = condition;
    }

    @Override
    public Schema transform(Schema inputSchema) {
        //Conditional replace should not change any of the metadata, under normal usage
        return inputSchema;
    }

    @Override
    public void setInputSchema(Schema inputSchema){
        this.filterColIdx = inputSchema.getColumnNames().indexOf(columnToReplace);
        if (this.filterColIdx < 0) {
            throw new IllegalStateException("Column \"" + columnToReplace + "\" not found in input schema");
        }
        condition.setInputSchema(inputSchema);
    }


    @Override
    public Schema getInputSchema() {
        return condition.getInputSchema();
    }

    @Override
    public String outputColumnName() {
        return columnToReplace;
    }

    @Override
    public String[] outputColumnNames() {
        return columnNames();
    }

    @Override
    public String[] columnNames() {
        return new String[] {columnToReplace};
    }

    @Override
    public String columnName() {
        return columnToReplace;
    }

    @Override
    public String toString() {
        return "ConditionalReplaceValueTransformWithDefault(replaceColumn=\"" + columnToReplace
            + "\",newValue=" + newVal
            + "\",defaultvalue=" + defaultVal
            + ",condition=" + condition + ")";
    }


    @Override
    public List<List<Writable>> mapSequence(List<List<Writable>> sequence) {
        List<List<Writable>> out = new ArrayList<>();
        for (List<Writable> step : sequence) {
            out.add(map(step));
        }
        return out;
    }

    @Override
    public Object map(Object input) {
        if (condition.condition(input)){
            return newVal;
        } else {
            return defaultVal;
        }
    }

    @Override
    public Object mapSequence(Object sequence) {
        List<?> seq = (List<?>) sequence;
        List<Object> out = new ArrayList<>();
        for (Object step : seq) {
            out.add(map(step));
        }
        return out;
    }

    @Override
    public List<Writable> map(List<Writable> writables) {
        if (condition.condition(writables)) {
            //Condition holds -> set new value
            List<Writable> newList = new ArrayList<>(writables);
            newList.set(filterColIdx, newVal);
            return newList;
        } else {
            //Condition does not hold -> set default value
            List<Writable> newList = new ArrayList<>(writables);
            newList.set(filterColIdx, defaultVal);
            return newList;
        }
    }


}
