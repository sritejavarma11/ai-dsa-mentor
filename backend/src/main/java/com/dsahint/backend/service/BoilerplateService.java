package com.dsahint.backend.service;

import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class BoilerplateService {

    public String normalizeLanguage(String language) {
        if (language == null) return "java";
        String l = language.trim().toLowerCase(Locale.ROOT);
        return switch (l) {
            case "py" -> "python";
            case "c++", "cpp" -> "cpp";
            case "js", "javascript" -> "javascript";
            default -> l;
        };
    }

    public String monacoLanguage(String language) {
        String l = normalizeLanguage(language);
        return switch (l) {
            case "cpp" -> "cpp";
            case "python" -> "python";
            case "java" -> "java";
            case "javascript" -> "javascript";
            default -> "java";
        };
    }

    public String boilerplateFor(String language, String queryOrProblem) {
        String l = normalizeLanguage(language);
        String q = queryOrProblem == null ? "" : queryOrProblem.toLowerCase(Locale.ROOT);

        if (looksLikeTwoSum(q)) {
            return twoSumTemplate(l);
        }
        if (q.contains("dfs") || q.contains("depth first")) {
            return dfsTemplate(l);
        }
        if (q.contains("bfs") || q.contains("breadth first")) {
            return bfsTemplate(l);
        }

        return genericTemplate(l);
    }

    public String fallbackProblemFor(String query) {
        String q = query == null ? "" : query.toLowerCase(Locale.ROOT).trim();
        if (looksLikeTwoSum(q) || q.contains("leetcode 1") || q.contains("two sum")) {
            return """
                    Given an array of integers nums and an integer target, return indices of the two numbers such that they add up to target.

                    You may assume that each input would have exactly one solution, and you may not use the same element twice.
                    You can return the answer in any order.

                    Example:
                    nums = [2,7,11,15], target = 9 -> [0,1]
                    """.trim();
        }
        if (q.contains("dfs") || q.contains("depth first")) {
            return """
                    Given an undirected graph with n nodes (0..n-1) and an edge list, return a DFS traversal starting from node 0.

                    Input: n, edges (pairs)
                    Output: list of visited nodes in the order they are first visited.

                    Handle disconnected graphs by only traversing nodes reachable from 0.
                    """.trim();
        }
        if (q.contains("bfs") || q.contains("breadth first")) {
            return """
                    Given an undirected graph with n nodes (0..n-1) and an edge list, return a BFS traversal starting from node 0.

                    Input: n, edges (pairs)
                    Output: list of visited nodes in the order they are first visited.

                    Handle disconnected graphs by only traversing nodes reachable from 0.
                    """.trim();
        }
        return """
                Write a function to solve the described problem efficiently.

                Provide a clear approach, handle edge cases, and return the required output format.
                """.trim();
    }

    private boolean looksLikeTwoSum(String q) {
        return q.contains("two sum")
                || (q.contains("two") && q.contains("target") && (q.contains("array") || q.contains("nums")))
                || q.contains("add up to a target");
    }

    private String genericTemplate(String language) {
        return switch (language) {
            case "python" -> """
                    from typing import *

                    def solve():
                        # TODO: read input, implement logic, print output
                        pass

                    if __name__ == "__main__":
                        solve()
                    """.trim();
            case "cpp" -> """
                    #include <bits/stdc++.h>
                    using namespace std;

                    int main() {
                        ios::sync_with_stdio(false);
                        cin.tie(nullptr);

                        // TODO: read input, implement logic, print output

                        return 0;
                    }
                    """.trim();
            case "javascript" -> """
                    function solve() {
                      // TODO: read input, implement logic, print output
                    }

                    solve();
                    """.trim();
            default -> """
                    import java.io.*;
                    import java.util.*;

                    public class Main {
                        public static void main(String[] args) throws Exception {
                            FastScanner fs = new FastScanner(System.in);
                            // TODO: read input, implement logic, print output
                        }

                        static class FastScanner {
                            private final InputStream in;
                            private final byte[] buffer = new byte[1 << 16];
                            private int ptr = 0, len = 0;
                            FastScanner(InputStream is) { in = is; }
                            private int read() throws IOException {
                                if (ptr >= len) {
                                    len = in.read(buffer);
                                    ptr = 0;
                                    if (len <= 0) return -1;
                                }
                                return buffer[ptr++];
                            }
                            String next() throws IOException {
                                StringBuilder sb = new StringBuilder();
                                int c;
                                while ((c = read()) != -1 && c <= ' ') {}
                                if (c == -1) return null;
                                do {
                                    sb.append((char) c);
                                } while ((c = read()) != -1 && c > ' ');
                                return sb.toString();
                            }
                            int nextInt() throws IOException { return Integer.parseInt(next()); }
                        }
                    }
                    """.trim();
        };
    }

    private String twoSumTemplate(String language) {
        return switch (language) {
            case "python" -> """
                    from typing import List

                    class Solution:
                        def twoSum(self, nums: List[int], target: int) -> List[int]:
                            # TODO: implement
                            return []
                    """.trim();
            case "cpp" -> """
                    #include <bits/stdc++.h>
                    using namespace std;

                    class Solution {
                    public:
                        vector<int> twoSum(vector<int>& nums, int target) {
                            // TODO: implement
                            return {};
                        }
                    };
                    """.trim();
            case "javascript" -> """
                    /**
                     * @param {number[]} nums
                     * @param {number} target
                     * @return {number[]}
                     */
                    function twoSum(nums, target) {
                      // TODO: implement
                      return [];
                    }
                    """.trim();
            default -> """
                    import java.util.*;

                    class Solution {
                        public int[] twoSum(int[] nums, int target) {
                            // TODO: implement
                            return new int[0];
                        }
                    }
                    """.trim();
        };
    }

    private String dfsTemplate(String language) {
        return switch (language) {
            case "python" -> """
                    from typing import List

                    def dfs_traversal(n: int, edges: List[List[int]]) -> List[int]:
                        # TODO: build adjacency list and run DFS from 0
                        return []
                    """.trim();
            case "cpp" -> """
                    #include <bits/stdc++.h>
                    using namespace std;

                    vector<int> dfs_traversal(int n, vector<vector<int>>& edges) {
                        // TODO: build adjacency list and run DFS from 0
                        return {};
                    }
                    """.trim();
            case "javascript" -> """
                    /**
                     * @param {number} n
                     * @param {number[][]} edges
                     * @return {number[]}
                     */
                    function dfsTraversal(n, edges) {
                      // TODO: build adjacency list and run DFS from 0
                      return [];
                    }
                    """.trim();
            default -> """
                    import java.util.*;

                    public class Solution {
                        public List<Integer> dfsTraversal(int n, int[][] edges) {
                            // TODO: build adjacency list and run DFS from 0
                            return new ArrayList<>();
                        }
                    }
                    """.trim();
        };
    }

    private String bfsTemplate(String language) {
        return switch (language) {
            case "python" -> """
                    from collections import deque
                    from typing import List

                    def bfs_traversal(n: int, edges: List[List[int]]) -> List[int]:
                        # TODO: build adjacency list and run BFS from 0
                        return []
                    """.trim();
            case "cpp" -> """
                    #include <bits/stdc++.h>
                    using namespace std;

                    vector<int> bfs_traversal(int n, vector<vector<int>>& edges) {
                        // TODO: build adjacency list and run BFS from 0
                        return {};
                    }
                    """.trim();
            case "javascript" -> """
                    /**
                     * @param {number} n
                     * @param {number[][]} edges
                     * @return {number[]}
                     */
                    function bfsTraversal(n, edges) {
                      // TODO: build adjacency list and run BFS from 0
                      return [];
                    }
                    """.trim();
            default -> """
                    import java.util.*;

                    public class Solution {
                        public List<Integer> bfsTraversal(int n, int[][] edges) {
                            // TODO: build adjacency list and run BFS from 0
                            return new ArrayList<>();
                        }
                    }
                    """.trim();
        };
    }
}

