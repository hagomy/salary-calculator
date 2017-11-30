/*
 * Copyright 2017 haeun kim
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Created by haeun on 2017-11-29
 */
import db.DBConnManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBSample2 {

    Connection conn = null;

    // 생성자
    DBSample2() {
    }

    // 소멸자
    protected void finalize() {
        closeDBConnection();
    }

    // DB 연결이 유효한지 확인. 유효하지 않으면 새로운 연결 수립
    void validateDBConnection() {
        try {
            if (conn == null) {
                conn = DBConnManager.getConnection();
                System.err.println("DB가 연결되었습니다.");
            } else if (!conn.isValid(15)) { // 15초 이내에 정상적인 응답이 없으면,
                conn.close();
                conn = DBConnManager.getConnection();
                System.err.println("DB가 재연결되었습니다.");
            }
        } catch (SQLException e) {
            System.err.println("DB에 접근할 수 없습니다.");
            e.printStackTrace();
        }
    }

    // DB 연결 해제
    void closeDBConnection() {
        try {
            DBConnManager.closeConnection(conn);
            System.err.println("DB 연결이 해제되었습니다.");
        } catch (SQLException e) {
            System.err.println("DB 연결 해제 중 에러!");
            e.printStackTrace();
        }
    }

    public void CeoSelect() { // 사장이 본인 근무지의 직원들을 모아보기
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // 1-1 사장으로부터 검색할 근무지명을 입력받음
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("검색할 근무지명을 입력하세요. 근무지명: ");
            String str = reader.readLine();

            // 2. DB 연결 확인
            validateDBConnection();

            // 3. SQL 실행
            stmt = conn.prepareStatement("select 근무지명, 직원번호, 이름, 전화번호, 근무시간, 시급 from 직원 where 근무지명 = ?;");
            stmt.setString(1, str);
            rs = stmt.executeQuery();

            // 4. Result Set 처리
            System.out.println("근무지명\t\t직원번호\t이름\t전화번호\t\t근무시간\t시급\t월급");
            System.out.println("-----------------------------------------------------------------------");
            while (rs.next()) {
                System.out.println(rs.getString(1) + "\t" + rs.getInt(2) + "\t" + rs.getString(3) + "\t" + rs.getInt(4)
                        + "\t" + rs.getInt(5) + "\t" + rs.getInt(6) + "\t" + rs.getInt(5) * rs.getInt(6));
            }
        } catch (IOException e) {
            System.out.println("입력 에러!");
        } catch (SQLException e) {
            System.out.println("DB에 접근할 수 없거나 SQL을 실행할 수 없습니다.");
            e.printStackTrace();
        }
        // 5. 리소스 반환
        finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (Exception e) {
                }
            if (stmt != null)
                try {
                    stmt.close();
                } catch (Exception e) {
                }
        }
    }

    public void EmpSelect() throws IOException { // 직원번호를 입력하면 해당 직원이 나오는 메소드
        PreparedStatement stmt = null;
        ResultSet rs = null;

        // 2. DB 연결 확인
        validateDBConnection();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("본인의 직원번호를 입력하세요. 직원번호: ");
            String str = reader.readLine();

            // 3. SQL 실행
            stmt = conn.prepareStatement("select 직원번호, 이름, 전화번호, 근무시간, 시급, 근무지명 from 직원 where 직원번호 = ?;");
            stmt.setInt(1, Integer.parseInt(str));
            rs = stmt.executeQuery();

            // 4. Result Set 처리
            System.out.println("직원번호\t이름\t전화번호\t\t근무시간\t시급\t월급\t근무지명");
            System.out.println("-----------------------------------------------------------------------");
            while (rs.next()) {
                System.out.println(rs.getInt(1) + "\t" + rs.getString(2) + "\t" + rs.getString(3) + "\t" + rs.getInt(4)
                        + "\t" + rs.getInt(5) + "\t" + rs.getInt(4) * rs.getInt(5) + "\t" + rs.getString(6));
            }
        } catch (SQLException e) {
            System.err.println("DB에 접근할 수 없거나 SQL을 실행할 수 없습니다.");
            e.printStackTrace();
        }
        // 5. 리소스 반환
        finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (Exception e) {
                }
            if (stmt != null)
                try {
                    stmt.close();
                } catch (Exception e) {
                }
        }
    }

    public void EmpDelete() throws IOException { // 직원의 회원탈퇴
        PreparedStatement stmt = null;
        ResultSet rs = null;

        // 2. DB 연결 확인
        validateDBConnection();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("회원탈퇴입니다. 본인의 직원번호를 입력하세요. 직원번호: ");
            String str = reader.readLine();

            // 3. SQL 실행
            stmt = conn.prepareStatement("delete from 직원 where 직원번호 = ?;");
            stmt.setInt(1, Integer.parseInt(str));
            stmt.executeUpdate();

            System.out.println("탈퇴가 완료되었습니다.");
        } catch (SQLException e) {
            System.err.println("DB에 접근할 수 없거나 SQL을 실행할 수 없습니다.");
            e.printStackTrace();
        }
        // 5. 리소스 반환
        finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (Exception e) {
                }
            if (stmt != null)
                try {
                    stmt.close();
                } catch (Exception e) {
                }
        }
    }

    public void EmpUpdate() { // 직원이 근무시간을 입력하면 해당 직원의 레코드를 수정한다.
        PreparedStatement stmt = null;
        ResultSet rs = null;

        // 2. DB 연결 확인
        validateDBConnection();

        try {
            // 3. SQL 실행
            stmt = conn.prepareStatement("select 직원번호, 이름, 전화번호, 근무시간, 시급, 근무지명 from 직원;");
            rs = stmt.executeQuery();

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("본인의 직원번호를 입력하세요. 직원번호: ");
            String str = reader.readLine();

            stmt = conn.prepareStatement("select 직원번호, 이름, 전화번호, 근무시간, 시급, 근무지명 from 직원 where 직원번호 = ?;");
            stmt.setInt(1, Integer.parseInt(str));
            rs = stmt.executeQuery();

            // 4. Result Set 처리
            System.out.println("직원번호\t이름\t전화번호\t\t근무시간\t시급\t월급\t근무지명");
            System.out.println("-----------------------------------------------------------------------");
            while (rs.next()) {
                System.out.println(rs.getInt(1) + "\t" + rs.getString(2) + "\t" + rs.getString(3) + "\t" + rs.getInt(4)
                        + "\t" + rs.getInt(5) + "\t" + rs.getInt(4) * rs.getInt(5) + "\t" + rs.getString(6));

                int a = rs.getInt(4);

                BufferedReader reader1 = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("추가 근무시간 입력: ");

                a += reader1.read();
                a -= 48;

                stmt = conn.prepareStatement("update 직원 set 근무시간 = ? where 직원번호 = ?;");
                stmt.setInt(1, a);
                stmt.setInt(2, Integer.parseInt(str));
                stmt.executeUpdate();

                stmt = conn.prepareStatement("select 직원번호, 이름, 전화번호, 근무시간, 시급, 근무지명 from 직원 where 직원번호 = ?;");
                stmt.setInt(1, Integer.parseInt(str));
                rs = stmt.executeQuery();

                System.out.println("직원번호\t이름\t전화번호\t\t근무시간\t시급\t월급\t근무지명");
                System.out.println("-----------------------------------------------------------------------");
                while (rs.next()) {
                    System.out.println(
                            rs.getInt(1) + "\t" + rs.getString(2) + "\t" + rs.getString(3) + "\t" + rs.getInt(4) + "\t"
                                    + rs.getInt(5) + "\t" + rs.getInt(4) * rs.getInt(5) + "\t" + rs.getString(6));
                }
            }
        } catch (IOException e) {
            System.out.println("입력 에러!");
        } catch (SQLException e) {
            System.out.println("DB에 접근할 수 없거나 SQL을 실행할 수 없습니다.");
            e.printStackTrace();
        }

        // 5. 리소스 반환
        finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (Exception e) {
                }
            if (stmt != null)
                try {
                    stmt.close();
                } catch (Exception e) {
                }
        }

    }

    public void EmpInsert() throws IOException { // 새로운 회원의 정보를 삽입한다.
        PreparedStatement stmt = null;
        ResultSet rs = null;

        // 2. DB 연결 확인
        validateDBConnection();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("이름을 입력하세요: ");
            String nameStr = reader.readLine();
            System.out.print("전화번호를 입력하세요: ");
            String phoneStr = reader.readLine();
            System.out.print("근무시간을 입력하세요: ");
            String workStr = reader.readLine();
            System.out.print("시급을 입력하세요: ");
            String feeStr = reader.readLine();
            System.out.print("근무지명을 입력하세요: ");
            String wnameStr = reader.readLine();

            stmt = conn.prepareStatement("insert 직원(이름, 전화번호, 근무시간, 시급, 근무지명) values(?, ?, ?, ?, ?);");

            stmt.setString(1, nameStr);
            stmt.setInt(2, Integer.parseInt(phoneStr));
            stmt.setInt(3, Integer.parseInt(workStr));
            stmt.setInt(4, Integer.parseInt(feeStr));
            stmt.setString(5, wnameStr);

            stmt.executeUpdate();

            stmt = conn.prepareStatement("select * from 직원;");
            rs = stmt.executeQuery();
            while (rs.next()) {
                if (rs.last()) {
                    System.out.println("등록완료! *기억하세요* 본인의 직원번호는 " + rs.getInt(1) + "입니다.");
                }
            }
        } catch (SQLException e) {
            System.err.println("DB에 접근할 수 없거나 SQL을 실행할 수 없습니다.");
            e.printStackTrace();
        }
        // 5. 리소스 반환
        finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (Exception e) {
                }
            if (stmt != null)
                try {
                    stmt.close();
                } catch (Exception e) {
                }
        }
    }

    public static void main(String[] args) throws IOException {
        DBSample2 sample = new DBSample2();

        while (true) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("사장이면 1을, 직원이면 2를, 종료하려면 3을 입력해주세요 : ");
            int s = br.read() - 48;
            if (s == 1) {
                sample.CeoSelect();
            } else if (s == 2) {
                BufferedReader br2 = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("회원등록 1, 회원탈퇴 2, 근무시간입력 3, 월급확인 4 중에 원하는 숫자를 입력하세요 : ");
                int b = br2.read() - 48;
                switch (b) {
                    case 1:
                        sample.EmpInsert();
                        break;
                    case 2:
                        sample.EmpDelete();
                        break;
                    case 3:
                        sample.EmpUpdate();
                        break;
                    case 4:
                        sample.EmpSelect();
                        break;
                }
            } else {
                System.exit(0);
            }
            sample.closeDBConnection();
        }
    }
}
