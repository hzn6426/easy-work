/*
 * Copyright (c) 2025-2026, zening (316279828@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package work;

import com.baomibing.work.context.WorkContext;
import com.baomibing.work.work.Work;

public class UserPrintWork implements Work {

    private User user;

    public UserPrintWork(User user) {
        this.user = user;
    }

    @Override
    public Object execute(WorkContext context) {
        System.out.println("user name is " + user.getName() + ", age is " + user.getAge());
        return user;
    }
}
