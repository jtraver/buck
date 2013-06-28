/*
 * Copyright 2013-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.facebook.buck.cli;

import com.facebook.buck.event.BuckEvent;
import com.facebook.buck.model.BuildTarget;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * Events for timing the starting of android events.
 */
public abstract class StartActivityEvent extends BuckEvent {
  private final BuildTarget buildTarget;
  private final String activityName;

  protected StartActivityEvent(BuildTarget buildTarget, String activityName) {
    this.buildTarget = Preconditions.checkNotNull(buildTarget);
    this.activityName = Preconditions.checkNotNull(activityName);
  }

  public BuildTarget getBuildTarget() {
    return buildTarget;
  }

  public String getActivityName() {
    return activityName;
  }

  @Override
  protected String getValueString() {
    return String.format("%s %s", getBuildTarget().getFullyQualifiedName(), getActivityName());
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof StartActivityEvent)) {
      return false;
    }

    StartActivityEvent that = (StartActivityEvent)o;

    return Objects.equal(getClass(), o.getClass()) &&
        Objects.equal(getBuildTarget(), that.getBuildTarget()) &&
        Objects.equal(getActivityName(), that.getActivityName());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getActivityName(), getBuildTarget());
  }

  public static Started started(BuildTarget buildTarget, String activityName) {
    return new Started(buildTarget, activityName);
  }

  public static Finished finished(BuildTarget buildTarget, String activityName, boolean success) {
    return new Finished(buildTarget, activityName, success);
  }

  public static class Started extends StartActivityEvent {
    protected Started(BuildTarget buildTarget, String activityName) {
      super(buildTarget, activityName);
    }

    @Override
    protected String getEventName() {
      return "StartActivityStarted";
    }
  }

  public static class Finished extends StartActivityEvent {
    private final boolean success;

    protected Finished(BuildTarget buildTarget, String activityName, boolean success) {
      super(buildTarget, activityName);
      this.success = success;
    }

    public boolean isSuccess() {
      return success;
    }

    @Override
    protected String getEventName() {
      return "StartActivityFinished";
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }

      Finished that = (Finished) o;
      return isSuccess() == that.isSuccess();
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(getActivityName(), getBuildTarget(), isSuccess());
    }
  }
}
