/***** BEGIN LICENSE BLOCK *****
 * Version: CPL 1.0/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Common Public
 * License Version 1.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.eclipse.org/legal/cpl-v10.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either of the GNU General Public License Version 2 or later (the "GPL"),
 * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the CPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the CPL, the GPL or the LGPL.
 ***** END LICENSE BLOCK *****/
package org.jruby.runtime.profile;

import java.io.PrintStream;
import java.text.DecimalFormat;

import org.jruby.Ruby;
import org.jruby.RubyIO;

public class AbstractProfilePrinter {
    private final Ruby runtime;

    public AbstractProfilePrinter(Ruby runtime) {
        this.runtime = runtime;
    }

    public void printProfile(PrintStream out) {
    }

    public void printProfile(RubyIO out) {
        printProfile(new PrintStream(out.getOutStream()));
    }

    protected void pad(PrintStream out, int size, String body) {
        pad(out, size, body, true);
    }

    protected void pad(PrintStream out, int size, String body, boolean front) {
        if (front) {
            for (int i = 0; i < size - body.length(); i++) {
                out.print(' ');
            }
        }
        out.print(body);
        if (!front) {
            for (int i = 0; i < size - body.length(); i++) {
                out.print(' ');
            }
        }
    }

    protected String nanoString(long nanoTime) {
        DecimalFormat formatter = new DecimalFormat("##0.00");
        return formatter.format((double) nanoTime / 1.0E9);
    }

    public boolean isProfilerInvocation(Invocation inv) {
        return isThisProfilerInvocation(inv.getMethodSerialNumber()) || 
                (inv.getParent() != null && isProfilerInvocation(inv.getParent()));
    }
    
    public boolean isThisProfilerInvocation(int serial) {
        String name = methodName(serial);
        return name.length() > 15 && 
                (name.equals("JRuby::Profiler.start") || name.equals("JRuby::Profiler.stop"));
    }
    
    public String methodName(int serial) {
        return runtime.getMethodName(serial);
    }
}
