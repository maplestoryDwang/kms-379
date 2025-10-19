package constants;

public class undefinedIDA {
   public static boolean sub_140A90650(int a1) {
      int v1 = a1 - 5121007;
      boolean result = true;
      if (v1 == 1) {
         int v2 = v1 - 19993;
         if (v2 == 1 && v2 != 2) {
            result = false;
         }
      }

      return result;
   }

   public static boolean sub_1409CEA40(int a1) {
      boolean v2;
      int v3;
      if (a1 > 400041005) {
         v3 = a1 - 500061025;
         v2 = v3 == 0;
      } else {
         if (a1 == 400041005) {
            return true;
         }

         int v1 = a1 - 400041002;
         if (v1 == 0) {
            return true;
         }

         v3 = v1 - 1;
         v2 = v3 == 0;
      }

      return v2 || v3 == 1;
   }

   public static boolean sub_140A6C0C0(int a1) {
      return a1 == 21121057 || a1 == 13121052 || a1 == 14121052 || a1 == 15121052 || a1 == 80001431 || a1 == 80003084 || a1 == 100001283;
   }

   public static boolean KadenaUnk(int a1) {
      int v1 = a1 - 64001009;
      boolean result = true;
      if (v1 == 1) {
         int v2 = v1 - 1;
         if (v2 == 1 && v2 != 1) {
            result = false;
         }
      }

      return result;
   }

   public static boolean BlasterUnk(int a1) {
      int v1 = a1 - 37100002;
      boolean result = true;
      if (v1 == 1) {
         int v2 = v1 - 9999;
         if (v2 == 1 && v2 != 3) {
            result = false;
         }
      }

      return result;
   }

   public static boolean AranSkill(int a1) {
      if (a1 > 21110028) {
         if (a1 == 21120025 || a1 == 21141501 || a1 == 21141502 || a1 == 21141503) {
            return true;
         }

         boolean v1 = a1 == 21141504;
         if (v1) {
            return true;
         }
      }

      if (a1 == 21110028) {
         return true;
      } else if (a1 > 21110022) {
         if (a1 != 21110023) {
            boolean v1 = a1 == 21110026;
            if (v1) {
               return true;
            }
         }

         return true;
      } else if (a1 != 21110022 && a1 != 21000006 && a1 != 21000007) {
         boolean v1 = a1 == 21001010;
         switch (a1) {
            case 80001925:
            case 80001926:
            case 80001927:
            case 80001936:
            case 80001937:
            case 80001938:
               return true;
            case 80001928:
            case 80001929:
            case 80001930:
            case 80001931:
            case 80001932:
            case 80001933:
            case 80001934:
            case 80001935:
            default:
               return false;
         }
      } else {
         return true;
      }
   }

   public static boolean isUnknown379(int skillId) {
      return sub_140A5F750(skillId) && sub_140A600D0(skillId) && skillId == 152141001;
   }

   public static boolean sub_140A5F750(int a1) {
      boolean v2;
      if (a1 > 61121217) {
         int v3 = a1 - 400011058;
         if (a1 - 400011058 == 0) {
            return true;
         }

         v2 = v3 == 1;
      } else {
         if (a1 == 61121217 || a1 == 61101002 || a1 == 61110211 || a1 == 61120007 || a1 == 36001005 || a1 == 36100010 || a1 == 36110012) {
            return true;
         }

         v2 = a1 == 36120015;
      }

      if (!v2 && a1 != 4100012 && a1 != 4120019 && a1 != 35101002 && !sub_140A87740(a1)) {
         boolean v4;
         if (a1 > 155141002) {
            if (a1 == 155141009 || a1 == 155141013) {
               return true;
            }

            v4 = a1 == 155141018;
         } else {
            if (a1 == 155141002 || a1 == 155001000 || a1 == 155101002 || a1 == 155111003) {
               return true;
            }

            v4 = a1 == 155121003;
         }

         if (!v4 && !sub_140ABF9C0(a1) && !sub_140A66CE0(a1) && a1 != 3011004 && a1 != 3300002 && a1 != 3321003) {
            boolean v5;
            if (a1 > 31221014) {
               if (a1 > 164120007) {
                  if (a1 > 400031054) {
                     if (a1 > 400041068) {
                        int v36 = a1 - 400051017;
                        if (a1 - 400051017 == 0) {
                           return true;
                        }

                        int v37 = v36 - 100009998;
                        if (v36 - 100009998 == 0) {
                           return true;
                        }

                        int v38 = v37 - 2;
                        if (v37 - 2 == 0) {
                           return true;
                        }

                        v5 = v38 == 17;
                     } else {
                        if (a1 == 400041068) {
                           return true;
                        }

                        int v33 = a1 - 400041010;
                        if (a1 - 400041010 == 0) {
                           return true;
                        }

                        int v34 = v33 - 13;
                        if (v33 - 13 == 0) {
                           return true;
                        }

                        int v35 = v34 - 15;
                        if (v34 - 15 == 0) {
                           return true;
                        }

                        v5 = v35 == 11;
                     }
                  } else {
                     if (a1 == 400031054) {
                        return true;
                     }

                     if (a1 > 400031020) {
                        int v30 = a1 - 400031021;
                        if (v30 == 0) {
                           return true;
                        }

                        int v31 = v30 - 1;
                        if (v31 == 0) {
                           return true;
                        }

                        int v32 = v31 - 7;
                        if (v32 == 0) {
                           return true;
                        }

                        v5 = v32 == 2;
                     } else {
                        if (a1 == 400031020) {
                           return true;
                        }

                        int v27 = a1 - 400011131;
                        if (v27 == 0) {
                           return true;
                        }

                        int v28 = v27 - 9870;
                        if (v28 == 0) {
                           return true;
                        }

                        int v29 = v28 - 44;
                        if (v29 == 0) {
                           return true;
                        }

                        v5 = v29 == 9955;
                     }
                  }
               } else {
                  if (a1 == 164120007) {
                     return true;
                  }

                  if (a1 > 135002015) {
                     if (a1 > 152141000) {
                        int v24 = a1 - 152141001;
                        if (v24 == 0) {
                           return true;
                        }

                        int v25 = v24 - 1;
                        if (v25 == 0) {
                           return true;
                        }

                        int v26 = v25 - 2959007;
                        if (v26 == 0) {
                           return true;
                        }

                        v5 = v26 == 9000995;
                     } else {
                        if (a1 == 152141000) {
                           return true;
                        }

                        int v21 = a1 - 142110011;
                        if (v21 == 0) {
                           return true;
                        }

                        int v22 = v21 - 9890990;
                        if (v22 == 0) {
                           return true;
                        }

                        int v23 = v22 - 119000;
                        if (v23 == 0) {
                           return true;
                        }

                        v5 = v23 == 1;
                     }
                  } else {
                     if (a1 == 135002015
                        || a1 == 65141502
                        || a1 == 31241001
                        || a1 == 36110004
                        || a1 == 65111007
                        || a1 == 65120011
                        || a1 == 80001588
                        || a1 == 80001890
                        || a1 == 80002811) {
                        return true;
                     }

                     v5 = a1 == 131003016;
                  }
               }
            } else {
               if (a1 == 31221014) {
                  return true;
               }

               if (a1 > 12141005) {
                  if (a1 > 14110034) {
                     if (a1 > 24120002) {
                        if (a1 == 24121011 || a1 == 25100010 || a1 == 25120115) {
                           return true;
                        }

                        v5 = a1 == 25141505;
                     } else {
                        if (a1 == 24120002) {
                           return true;
                        }

                        int v18 = a1 - 14110035;
                        if (v18 == 0) {
                           return true;
                        }

                        int v19 = v18 - 9983;
                        if (v19 == 0) {
                           return true;
                        }

                        int v20 = v19 - 2;
                        if (v20 == 0) {
                           return true;
                        }

                        v5 = v20 == 9979983;
                     }
                  } else {
                     if (a1 == 14110034) {
                        return true;
                     }

                     if (a1 > 13120003) {
                        int v15 = a1 - 13120010;
                        if (v15 == 0) {
                           return true;
                        }

                        int v16 = v15 - 1007;
                        if (v16 == 0) {
                           return true;
                        }

                        int v17 = v16 - 879011;
                        if (v17 == 0) {
                           return true;
                        }

                        v5 = v17 == 1;
                     } else {
                        if (a1 == 13120003) {
                           return true;
                        }

                        int v12 = a1 - 13100027;
                        if (v12 == 0) {
                           return true;
                        }

                        int v13 = v12 - 995;
                        if (v13 == 0) {
                           return true;
                        }

                        int v14 = v13 - 9000;
                        if (v14 == 0) {
                           return true;
                        }

                        v5 = v14 == 5;
                     }
                  }
               } else {
                  if (a1 == 12141005) {
                     return true;
                  }

                  if (a1 > 12110030) {
                     if (a1 > 12121057) {
                        int v9 = a1 - 12121059;
                        if (v9 == 0) {
                           return true;
                        }

                        int v10 = v9 - 19942;
                        if (v10 == 0) {
                           return true;
                        }

                        int v11 = v10 - 1;
                        if (v11 == 0) {
                           return true;
                        }

                        v5 = v11 == 2;
                     } else {
                        if (a1 == 12121057) {
                           return true;
                        }

                        int v6 = a1 - 12120010;
                        if (v6 == 0) {
                           return true;
                        }

                        int v7 = v6 - 7;
                        if (v7 == 0) {
                           return true;
                        }

                        int v8 = v7 - 2;
                        if (v8 == 0) {
                           return true;
                        }

                        v5 = v8 == 1;
                     }
                  } else {
                     if (a1 == 12110030
                        || a1 == 4210014
                        || a1 == 3100010
                        || a1 == 3120017
                        || a1 == 3300005
                        || a1 == 3301009
                        || a1 == 3321037
                        || a1 == 4220021
                        || a1 == 12000026
                        || a1 == 12100028) {
                        return true;
                     }

                     v5 = a1 == 12110028;
                  }
               }
            }

            if (!v5) {
               return false;
            }
         }

         return true;
      } else {
         return true;
      }
   }

   public static boolean sub_140A600D0(int a1) {
      boolean v1;
      if (a1 > 152141005) {
         if (a1 == 162121010 || a1 == 152141006 || a1 == 162101011 || a1 == 162111005 || a1 == 162121019) {
            return true;
         }

         v1 = a1 == 400021122;
      } else {
         if (a1 == 152141005) {
            return true;
         }

         if (a1 > 31241001) {
            int v2 = a1 - 152141001;
            if (v2 == 0) {
               return true;
            }

            v1 = v2 == 1;
         } else {
            if (a1 == 31241001 || a1 == 2121052 || a1 == 3341002) {
               return true;
            }

            v1 = a1 == 11121018;
         }
      }

      return v1;
   }

   public static boolean Unknown379Atom(int a1) {
      boolean v2;
      if (a1 > 61121217) {
         if (a1 - 400011058 == 0) {
            return true;
         }

         v2 = a1 - 400011058 == 1;
      } else {
         if (a1 == 61121217 || a1 == 61101002 || a1 == 61110211 || a1 == 61120007 || a1 == 36001005 || a1 == 36100010 || a1 == 36110012) {
            return true;
         }

         v2 = a1 == 36120015;
      }

      if (!v2 && a1 != 4100012 && a1 != 4120019 && a1 != 35101002 && a1 != 35110017 && !sub_140A87740(a1)) {
         boolean v4;
         if (a1 > 155141002) {
            if (a1 == 155141009 || a1 == 155141013) {
               return true;
            }

            v4 = a1 == 155141018;
         } else {
            if (a1 == 155141002 || a1 == 155001000 || a1 == 155101002 || a1 == 155111003) {
               return true;
            }

            v4 = a1 == 155121003;
         }

         if (!v4 && !sub_140ABF9C0(a1) && !sub_140A66CE0(a1) && a1 != 3011004 && a1 != 3300002 && a1 != 3321003) {
            boolean v5;
            if (a1 > 31221014) {
               if (a1 > 164120007) {
                  if (a1 > 400031054) {
                     if (a1 > 400041068) {
                        int v36 = a1 - 400051017;
                        if (a1 - 400051017 == 0) {
                           return true;
                        }

                        int v37 = v36 - 100009998;
                        if (v36 - 100009998 == 0) {
                           return true;
                        }

                        int v38 = v37 - 2;
                        if (v37 - 2 == 0) {
                           return true;
                        }

                        v5 = v38 == 17;
                     } else {
                        if (a1 == 400041068) {
                           return true;
                        }

                        int v33 = a1 - 400041010;
                        if (a1 - 400041010 == 0) {
                           return true;
                        }

                        int v34 = v33 - 13;
                        if (v33 - 13 == 0) {
                           return true;
                        }

                        int v35 = v34 - 15;
                        if (v34 - 15 == 0) {
                           return true;
                        }

                        v5 = v35 == 11;
                     }
                  } else {
                     if (a1 == 400031054) {
                        return true;
                     }

                     if (a1 > 400031020) {
                        int v30 = a1 - 400031021;
                        if (a1 - 400031021 == 0) {
                           return true;
                        }

                        int v31 = v30 - 1;
                        if (v30 - 1 == 0) {
                           return true;
                        }

                        int v32 = v31 - 7;
                        if (v31 - 7 == 0) {
                           return true;
                        }

                        v5 = v32 == 2;
                     } else {
                        if (a1 == 400031020) {
                           return true;
                        }

                        int v27 = a1 - 400011131;
                        if (a1 - 400011131 == 0) {
                           return true;
                        }

                        int v28 = v27 - 9870;
                        if (v27 - 9870 == 0) {
                           return true;
                        }

                        int v29 = v28 - 44;
                        if (v28 - 44 == 0) {
                           return true;
                        }

                        v5 = v29 == 9955;
                     }
                  }
               } else {
                  if (a1 == 164120007) {
                     return true;
                  }

                  if (a1 > 135002015) {
                     if (a1 > 152141000) {
                        int v24 = a1 - 152141001;
                        if (a1 - 152141001 == 0) {
                           return true;
                        }

                        int v25 = v24 - 1;
                        if (v24 - 1 == 0) {
                           return true;
                        }

                        int v26 = v25 - 2959007;
                        if (v25 - 2959007 == 0) {
                           return true;
                        }

                        v5 = v26 == 9000995;
                     } else {
                        if (a1 == 152141000) {
                           return true;
                        }

                        int v21 = a1 - 142110011;
                        if (a1 - 142110011 == 0) {
                           return true;
                        }

                        int v22 = v21 - 9890990;
                        if (v21 - 9890990 == 0) {
                           return true;
                        }

                        int v23 = v22 - 119000;
                        if (v22 - 119000 == 0) {
                           return true;
                        }

                        v5 = v23 == 1;
                     }
                  } else {
                     if (a1 == 135002015
                        || a1 == 65141502
                        || a1 == 31241001
                        || a1 == 36110004
                        || a1 == 65111007
                        || a1 == 65120011
                        || a1 == 80001588
                        || a1 == 80001890
                        || a1 == 80002811) {
                        return true;
                     }

                     v5 = a1 == 131003016;
                  }
               }
            } else {
               if (a1 == 31221014) {
                  return true;
               }

               if (a1 > 12141005) {
                  if (a1 > 14110034) {
                     if (a1 > 24120002) {
                        if (a1 == 24121011 || a1 == 25100010 || a1 == 25120115) {
                           return true;
                        }

                        v5 = a1 == 25141505;
                     } else {
                        if (a1 == 24120002) {
                           return true;
                        }

                        int v18 = a1 - 14110035;
                        if (a1 - 14110035 == 0) {
                           return true;
                        }

                        int v19 = v18 - 9983;
                        if (v18 - 9983 == 0) {
                           return true;
                        }

                        int v20 = v19 - 2;
                        if (v19 - 2 == 0) {
                           return true;
                        }

                        v5 = v20 == 9979983;
                     }
                  } else {
                     if (a1 == 14110034) {
                        return true;
                     }

                     if (a1 > 13120003) {
                        int v15 = a1 - 13120010;
                        if (a1 - 13120010 == 0) {
                           return true;
                        }

                        int v16 = v15 - 1007;
                        if (v15 - 1007 == 0) {
                           return true;
                        }

                        int v17 = v16 - 879011;
                        if (v16 - 879011 == 0) {
                           return true;
                        }

                        v5 = v17 == 1;
                     } else {
                        if (a1 == 13120003) {
                           return true;
                        }

                        int v12 = a1 - 13100027;
                        if (a1 - 13100027 == 0) {
                           return true;
                        }

                        int v13 = v12 - 995;
                        if (v12 - 995 == 0) {
                           return true;
                        }

                        int v14 = v13 - 9000;
                        if (v13 - 9000 == 0) {
                           return true;
                        }

                        v5 = v14 == 5;
                     }
                  }
               } else {
                  if (a1 == 12141005) {
                     return true;
                  }

                  if (a1 > 12110030) {
                     if (a1 > 12121057) {
                        int v9 = a1 - 12121059;
                        if (a1 - 12121059 == 0) {
                           return true;
                        }

                        int v10 = v9 - 19942;
                        if (v9 - 19942 == 0) {
                           return true;
                        }

                        int v11 = v10 - 1;
                        if (v10 - 1 == 0) {
                           return true;
                        }

                        v5 = v11 == 2;
                     } else {
                        if (a1 == 12121057) {
                           return true;
                        }

                        int v6 = a1 - 12120010;
                        if (a1 - 12120010 == 0) {
                           return true;
                        }

                        int v7 = v6 - 7;
                        if (v6 - 7 == 0) {
                           return true;
                        }

                        int v8 = v7 - 2;
                        if (v7 - 2 == 0) {
                           return true;
                        }

                        v5 = v8 == 1;
                     }
                  } else {
                     if (a1 == 12110030
                        || a1 == 4210014
                        || a1 == 3100010
                        || a1 == 3120017
                        || a1 == 3300005
                        || a1 == 3301009
                        || a1 == 3321037
                        || a1 == 4220021
                        || a1 == 12000026
                        || a1 == 12100028) {
                        return true;
                     }

                     v5 = a1 == 12110028;
                  }
               }
            }

            if (!v5) {
               return false;
            }
         }

         return true;
      } else {
         return true;
      }
   }

   public static boolean isTemporarySkill(int a1) {
      if (a1 <= 80001647) {
         if (a1 == 80001647) {
            return true;
         }

         boolean v1 = a1 == 80000330;
         if (!v1) {
            return false;
         }
      }

      if (a1 <= 80002338) {
         if (a1 != 80002338) {
            switch (a1) {
               case 80001648:
               case 80001649:
               case 80001650:
               case 80001651:
               case 80001652:
               case 80001653:
               case 80001654:
               case 80001655:
               case 80001656:
               case 80001657:
               case 80001658:
               case 80001659:
               case 80001660:
               case 80001661:
               case 80001662:
               case 80001663:
               case 80001664:
               case 80001665:
               case 80001666:
               case 80001667:
               case 80001668:
               case 80001669:
               case 80001670:
               case 80001675:
               case 80001676:
               case 80001677:
               case 80001678:
               case 80001679:
               case 80001732:
               case 80001733:
               case 80001734:
               case 80001735:
               case 80001736:
               case 80001737:
               case 80001738:
               case 80001739:
               case 80001740:
               case 80001741:
               case 80001742:
               case 80001743:
               case 80001744:
               case 80001745:
               case 80001746:
                  return true;
               case 80001671:
               case 80001672:
               case 80001673:
               case 80001674:
               case 80001680:
               case 80001681:
               case 80001682:
               case 80001683:
               case 80001684:
               case 80001685:
               case 80001686:
               case 80001687:
               case 80001688:
               case 80001689:
               case 80001690:
               case 80001691:
               case 80001692:
               case 80001693:
               case 80001694:
               case 80001695:
               case 80001696:
               case 80001697:
               case 80001698:
               case 80001699:
               case 80001700:
               case 80001701:
               case 80001702:
               case 80001703:
               case 80001704:
               case 80001705:
               case 80001706:
               case 80001707:
               case 80001708:
               case 80001709:
               case 80001710:
               case 80001711:
               case 80001712:
               case 80001713:
               case 80001714:
               case 80001715:
               case 80001716:
               case 80001717:
               case 80001718:
               case 80001719:
               case 80001720:
               case 80001721:
               case 80001722:
               case 80001723:
               case 80001724:
               case 80001725:
               case 80001726:
               case 80001727:
               case 80001728:
               case 80001729:
               case 80001730:
               case 80001731:
               default:
                  return false;
            }
         } else {
            return true;
         }
      } else if (a1 > 80002670) {
         if (a1 > 80003001) {
            switch (a1) {
               case 80003002:
               case 80003003:
               case 80003004:
               case 80003005:
               case 80003006:
               case 80003007:
               case 80003008:
               case 80003009:
               case 80003010:
               case 80003011:
               case 80003012:
               case 80003013:
                  return true;
               default:
                  return false;
            }
         } else if (a1 != 80003001) {
            switch (a1) {
               case 80002671:
               case 80002672:
               case 80002673:
               case 80002674:
               case 80002675:
               case 80002676:
               case 80002677:
               case 80002678:
               case 80002680:
               case 80002681:
                  return true;
               case 80002679:
               default:
                  return false;
            }
         } else {
            return true;
         }
      } else {
         if (a1 != 80002670) {
            int v3 = a1 - 80002339;
            if (v3 == 1) {
               int v4 = v3 - 1;
               if (v4 == 1) {
                  int v5 = v4 - 1;
                  if (v5 == 1) {
                     int v6 = v5 - 1;
                     if (v6 == 1) {
                        boolean v1 = v6 == 2;
                        if (!v1) {
                           return false;
                        }
                     }
                  }
               }
            }
         }

         return true;
      }
   }

   public static boolean sub_140A87740(int a1) {
      return a1 - 152141004 <= 2 || a1 == 152110004 || a1 == 152120016 || a1 == 155121003 || a1 == 155141018;
   }

   public static boolean sub_140ABF9C0(int a1) {
      return a1 == 22141017 || a1 == 22170070 || a1 == 63111010 || a1 == 155111207;
   }

   public static boolean sub_140A66CE0(int a1) {
      return a1 - 80002602 <= 19;
   }
}
